package org.rainboweleven.rbridge.impl;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.core.RBridgePlugin;
import org.rainboweleven.rbridge.core.RNativeInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * 系统自带WebView实现，支持传递参数为JSONObject类型，返回参数为PluginResult的插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:43 GMT+8
 * @email 411086563@qq.com
 */
public class RSystemWebView extends WebView implements RWebViewInterface, RNativeInterface {

    public RSystemWebView(Context context) {
        super(context);
        init();
    }

    public RSystemWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RSystemWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RSystemWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public RSystemWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    // 初始化
    private void init() {
        WebSettings settings = getSettings();

        // 设置是否允许访问Javascript
        settings.setJavaScriptEnabled(true);
        // 设置UserAgent
        settings.setUserAgentString("android_system_webview");
        // 设置缓存模式
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置是否允许访问文件
        settings.setAllowFileAccess(true);
        // 设置H5缓存API是否启用
        settings.setAppCacheEnabled(true);
        // 设置dom操作API是否启用
        settings.setDomStorageEnabled(true);
        // 设置数据库存储是否启用
        settings.setDatabaseEnabled(true);
        // 强制开启调试模式
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
        // 开启跨域访问
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        } else {
            try {
                Class<?> clazz = settings.getClass();
                Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(settings, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 5.0之后需要开启接受第三方cookie
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        settings.setLoadWithOverviewMode(true);
        // settings.setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setUseWideViewPort(true);
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // FIXME 这里待优化
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RBridgePluginManager.getInstance().onRWebViewReady(RSystemWebView.this);
                    }
                }, 1000);
            }
        });
        setWebViewClient(new WebViewClient());
        addJavascriptInterface(this, "nativeBridge");
    }

    @Override
    public void loadRemoteURL(String url, String hash) {
        if (TextUtils.isEmpty(hash)) {
            loadUrl(url);
        } else {
            loadUrl(url + "#" + hash);
        }
    }

    @Override
    public String evaluateJavascript(String script) {
        final String[] syncResult = {null};
        final CountDownLatch latch = new CountDownLatch(1);
        evaluateJavascript(script, new OnCallJsResultListener<String>() {
            @Override
            public void onCallJsResult(String result) {
                syncResult[0] = result;
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return syncResult[0];
    }

    @Override
    public void loadLocalURL(String url, String hash) {
        loadRemoteURL(url, hash);
    }

    @Override
    public void evaluateJavascript(final String script, final OnCallJsResultListener<String> listener) {
        // Runnable
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 执行JS代码
                evaluateJavascript(script, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String result) {
                        if (listener != null) {
                            listener.onCallJsResult(result);
                        }
                    }
                });
            }
        };
        // 主线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            // 非主线程
            post(runnable);
        }
    }

    @Override
    public void register(String module, String method, RBridgePlugin<?, ?> plugin) {
        // 交给插件管理器去注册
        RBridgePluginManager.getInstance().register(this, module, method, plugin);
    }

    @Override
    public void register(String module, String method, RBridgeAsyncPlugin<?, ?> plugin) {
        // 交给插件管理器去注册
        RBridgePluginManager.getInstance().register(this, module, method, plugin);
    }

    @Override
    public Context context() {
        return getContext();
    }

    @Override
    protected void onDetachedFromWindow() {
        RBridgePluginManager.getInstance().onRWebViewNotReady(this);
        super.onDetachedFromWindow();
    }

    @Override
    @JavascriptInterface
    public String call(String request) {
        String module = null;
        String method = null;
        String params = null;
        String jsCallback = null;
        try {
            JSONObject jsonObject = new JSONObject(request);
            module = jsonObject.optString("module");
            method = jsonObject.optString("method");
            params = jsonObject.optString("params");
            jsCallback = jsonObject.optString("callbackName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 同步
        if (TextUtils.isEmpty(jsCallback)) {
            // 交给插件管理器去执行
            return RBridgePluginManager.getInstance().runNativePlugin(module, method, params);
        }
        // 异步
        else {
            // 交给插件管理器去执行
            RBridgePluginManager.getInstance().runNativePlugin(this, module, method, params, jsCallback);
            return "";
        }
    }
}