package org.rainboweleven.rbridge.impl;

import android.content.Context;
import android.os.Build;
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
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        settings.setAllowFileAccess(false);
        settings.setAppCacheEnabled(false);
        settings.setSavePassword(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportMultipleWindows(true);
        if (Build.VERSION.SDK_INT >= 21) {
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
                }, 100);
            }
        });
        setWebViewClient(new WebViewClient());
        addJavascriptInterface(this, "nativeBridge");
        setWebContentsDebuggingEnabled(true);
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
