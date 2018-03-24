package org.rainboweleven.rbridge;

import android.text.TextUtils;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

/**
 * 对外提供的JsBridge操作，全部是静态方法，方便使用
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 16:09 GMT+8
 * @email 411086563@qq.com
 */
public class JsBridge {

    // 私有构造方法
    private JsBridge() {
    }

    /**
     * 加载本地页面
     *
     * @param webViewInterface
     * @param url
     * @param hash
     */
    public static void loadLocalURL(RWebViewInterface webViewInterface, String url, String hash) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.loadLocalURL(url, hash);
    }

    /**
     * 加载远程页面
     *
     * @param webViewInterface
     * @param url
     * @param hash
     */
    public static void loadRemoteURL(RWebViewInterface webViewInterface, String url, String hash) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.loadRemoteURL(url, hash);
    }

    /**
     * 原生插件注册给JS用
     *
     * @param webViewInterface
     * @param module
     * @param method
     * @param plugin
     */
    public static void register(RWebViewInterface webViewInterface, String module, String method, RWebkitPlugin
            plugin) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.register(module, method, plugin);
    }

    /**
     * 执行JS上的插件(方法)
     *
     * @param webViewInterface
     * @param module
     * @param method
     * @param params
     * @param listener
     */
    public static void call(RWebViewInterface webViewInterface, String module, String method, JSONObject params,
                            OnCallJsResultListener listener) {
        if (webViewInterface == null) {
            return;
        }
        String paramsStr = params.toString();
        if (TextUtils.isEmpty(module)) {
            module = RWebViewInterface.MODULE_DEFAULT;
        }
        // 执行 window.jsBridge.func.module.method(paramsStr)
        String script = String.format(RWebViewInterface.CALL_JS_BRIDGE_MODULE_FUNCTION, module, method, paramsStr);
        webViewInterface.evaluateJavascript(script, listener);
    }

    /**
     * 监听来自JS的事件
     *
     * @param webViewInterface
     * @param eventName
     * @param observer
     */
    public static void on(RWebViewInterface webViewInterface, String eventName, EventObserver observer) {
        if (webViewInterface == null) {
            return;
        }
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.on(eventName, observer);
    }

    /**
     * 解除监听来自JS的事件
     *
     * @param webViewInterface
     * @param eventName
     */
    public static void off(RWebViewInterface webViewInterface, String eventName, EventObserver observer) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.off(eventName, observer);
    }

    /**
     * 发送一个事件给JS
     *
     * @param webViewInterface
     * @param eventName
     * @param params
     */
    public static void send(RWebViewInterface webViewInterface, String eventName, String params) {
        if (webViewInterface == null) {
            return;
        }
        webViewInterface.send(eventName, params);
    }
}
