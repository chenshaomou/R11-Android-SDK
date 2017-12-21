package org.rainboweleven.rbridge.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin.OnCallPluginListener;
import org.rainboweleven.rbridge.core.RBridgePlugin;
import org.rainboweleven.rbridge.core.RNativeInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface;

/**
 * 系统自带WebView实现，支持传递参数为JSONObject类型，返回参数为PluginResult的插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:43 GMT+8
 * @email 411086563@qq.com
 */
public class RSystemWebView extends WebView implements RWebViewInterface<JsResult>, RNativeInterface {

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
        RSystemPluginManager.getInstance().initPlugins(this);
        RSystemPluginManager.getInstance().initEvents(this);
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
    public void loadLocalURL(String url, String hash) {
        loadRemoteURL(url, hash);
    }

    @Override
    public void evaluateJavascript(String script, final OnCallJsResultListener<JsResult> listener) {
        // 执行JS代码
        evaluateJavascript(script, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String result) {
                if (listener != null) {
                    JsResult jsResult = null;
                    if (!TextUtils.isEmpty(result)) {
                        JsResult.fromJsonStr(result);
                    }
                    listener.onCallJsResult(jsResult);
                }
            }
        });
    }

    @Override
    public void register(String module, String method, RBridgePlugin plugin) {
        // 交给插件管理器去注册
        RSystemPluginManager.getInstance().register(this, module, method, plugin);
    }

    @Override
    public void register(String module, String method, RBridgeAsyncPlugin plugin) {
        // 交给插件管理器去注册
        RSystemPluginManager.getInstance().register(this, module, method, plugin);
    }

    @Override
    @JavascriptInterface
    public String call(String module, String method, String params, String jsCallback) {
        // 将String参数转成JSONObject
        try {
            JSONObject jsonObjectParams = new JSONObject(params);
            if (TextUtils.isEmpty(jsCallback)) {
                // 同步
                return call(module, method, jsonObjectParams);
            } else {
                // 异步
                callAsync(module, method, jsonObjectParams, jsCallback);
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 同步执行插件
    private String call(String module, String action, JSONObject params) {
        // 交给插件管理器去执行
        PluginResult result = RSystemPluginManager.getInstance().runNativePlugin(module, action, params);
        if (result != null) {
            return result.toJsonString();
        } else {
            return "";
        }
    }

    // 异步步执行插件
    private void callAsync(String module, String action, JSONObject params, final String jsCallback) {
        // 交给插件管理器去执行
        RSystemPluginManager.getInstance().runNativePlugin(module, action, params, new
                OnCallPluginListener<PluginResult>() {
            @Override
            public void onCallPluginFinish() {
                String script = String.format(DELETE_JS_BRIDGE_CALLBACK, jsCallback);
                evaluateJavascript(script, (ValueCallback) null);
            }

            @Override
            public void onCallPluginResult(PluginResult result) {
                // 异步JS回调
                String resultStr = "";
                if (result != null) {
                    resultStr = result.toJsonString();
                }
                String script = String.format(CALL_JS_BRIDGE_CALLBACK, jsCallback, resultStr);
                evaluateJavascript(script, (ValueCallback) null);
            }
        });
    }
}
