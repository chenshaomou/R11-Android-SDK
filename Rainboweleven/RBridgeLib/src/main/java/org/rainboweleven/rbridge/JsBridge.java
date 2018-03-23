package org.rainboweleven.rbridge;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnEventReceivedListener;

/**
 * 对外提供的JsBridge操作
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 16:09 GMT+8
 * @email 411086563@qq.com
 */
public class JsBridge {

    // 单例实例
    private static JsBridge sInstance;

    /**
     * 获取单例
     *
     * @return
     */
    public static JsBridge getInstance() {
        if (sInstance == null) {
            synchronized (JsBridge.class) {
                if (sInstance == null) {
                    sInstance = new JsBridge();
                }
            }
        }
        return sInstance;
    }

    // 私有构造方法
    private JsBridge() {
    }

    /**
     * 读取本地页面
     *
     * @param webViewInterface
     * @param url
     * @param hash
     */
    public void loadLocalURL(RWebViewInterface webViewInterface, String url, String hash) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.loadLocalURL(url, hash);
    }

    /**
     * 读取远程页面
     *
     * @param webViewInterface
     * @param url
     * @param hash
     */
    public void loadRemoteURL(RWebViewInterface webViewInterface, String url, String hash) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.loadRemoteURL(url, hash);
    }

    /**
     * 注册原生插件给JS调用
     *
     * @param webViewInterface
     * @param module
     * @param method
     * @param plugin
     */
    public void register(RWebViewInterface webViewInterface, String module, String method, RBridgePlugin<?, ?> plugin) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.register(module, method, plugin);
    }

    /**
     * 注册异步原生插件给JS调用
     *
     * @param webViewInterface
     * @param module
     * @param method
     * @param plugin
     */
    public void register(RWebViewInterface webViewInterface, String module, String method, RBridgeAsyncPlugin<?, ?>
            plugin) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.register(module, method, plugin);
    }

    /**
     * 异步调用js插件
     *
     * @param webViewInterface
     * @param module
     * @param method
     * @param params
     * @param listener
     */
    public void call(RWebViewInterface webViewInterface, String module, String method, JSONObject params,
                     OnCallJsResultListener<String> listener) {
        if (webViewInterface == null) {
            return;
        }
        String script;
        String paramsStr = params.toString();
        if (TextUtils.isEmpty(module)) {
            module = RWebViewInterface.MODULE_DEFAULT;
        }
        // 执行 window.jsBridge.module.method(paramsStr)
        script = String.format(RWebViewInterface.CALL_JS_BRIDGE_MODULE_FUNCTION, module, method, paramsStr);
        webViewInterface.evaluateJavascript(script, listener);
    }

    /**
     * 监听事件
     *
     * @param webViewInterface
     * @param eventName
     * @param listener
     */
    public void on(RWebViewInterface webViewInterface, final String eventName, final OnEventReceivedListener<String>
            listener) {
        final String module = "event";
        final String method = "on";
        JSONObject params = new JSONObject();
        try {
            params.put("eventName", eventName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 执行 window.jsBridge.event.on(paramsStr)
        call(webViewInterface, module, method, params, new OnCallJsResultListener<String>() {
            @Override
            public void onCallJsResult(String result) {
                // 收到结果（事件）
                if (listener != null) {
                    listener.onEventReceived(module, method, result);
                }
            }
        });
    }

    /**
     * 解除监听
     *
     * @param webViewInterface
     * @param eventName
     */
    public void off(RWebViewInterface webViewInterface, String eventName) {
        final String module = "event";
        final String method = "off";
        JSONObject params = new JSONObject();
        try {
            params.put("eventName", eventName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 执行 window.jsBridge.event.off(paramsStr)
        call(webViewInterface, module, method, params, null);
    }

    /**
     * 发送事件
     *
     * @param webViewInterface
     * @param eventName
     * @param params
     */
    public void send(RWebViewInterface webViewInterface, String eventName, JSONObject params) {
        final String module = "event";
        final String method = "send";
        try {
            params.put("eventName", eventName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 执行 window.jsBridge.event.send(paramsStr)
        call(webViewInterface, module, method, params, null);
    }
}
