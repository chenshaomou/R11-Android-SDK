package org.rainboweleven.rbridge;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.core.RWebkitPlugin;
import org.rainboweleven.rbridge.impl.REventsCenter;

/**
 * 对外提供的JsBridge操作，全部是静态方法，方便使用
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 16:09 GMT+8
 * @email 411086563@qq.com
 */
public class JsBridge {

    // 单例
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
     * 加载本地页面
     *
     * @param webViewInterface
     * @param url
     * @param hash
     */
    public JsBridge loadLocalURL(RWebViewInterface webViewInterface, String url, String hash) {
        if (webViewInterface != null) {
            webViewInterface.loadLocalURL(url, hash);
        }
        return this;
    }

    /**
     * 加载远程页面
     *
     * @param webViewInterface
     * @param url
     * @param hash
     */
    public JsBridge loadRemoteURL(RWebViewInterface webViewInterface, String url, String hash) {
        if (webViewInterface != null) {
            webViewInterface.loadRemoteURL(url, hash);
        }
        return this;
    }

    /**
     * 原生插件注册给JS用
     *
     * @param webViewInterface
     * @param module
     * @param method
     * @param plugin
     */
    public JsBridge register(RWebViewInterface webViewInterface, String module, String method, RWebkitPlugin plugin) {
        if (webViewInterface != null) {
            webViewInterface.register(module, method, plugin);
        }
        return this;
    }

    /**
     * 执行JS上window.jsBridge.func的插件(方法)
     *
     * @param webViewInterface
     * @param method
     * @param params
     * @param listener
     */
    public JsBridge call(RWebViewInterface webViewInterface, String method, JSONObject params,
                         OnCallJsResultListener listener) {
        if (webViewInterface != null) {
            String paramsStr = params.toString();
            // 执行 window.jsBridge.func.module.method(paramsStr)
            String script = String.format(RWebViewInterface.CALL_JS_BRIDGE_MODULE_FUNCTION, method, paramsStr);
            webViewInterface.evaluateJavascript(script, listener);
        }
        return this;
    }

    /**
     * 监听整个系统的事件(含监听来自H5的事件)
     *
     * @param context
     * @param eventName
     * @param observer
     */
    public JsBridge on(Context context, String eventName, EventObserver observer) {
        REventsCenter.getInstance(context).on(eventName, observer);
        return this;
    }

    /**
     * 解除监听整个系统的事件(含取消监听来自H5的事件)
     *
     * @param context
     * @param eventName
     */
    public JsBridge off(Context context, String eventName, EventObserver observer) {
        REventsCenter.getInstance(context).off(eventName, observer);
        return this;
    }

    /**
     * 发送一个事件给整个系统(含发给H5)
     *
     * @param context
     * @param eventName
     * @param params
     */
    public JsBridge send(Context context, String eventName, String params) {
        REventsCenter.getInstance(context).send(eventName, params);
        return this;
    }

    /**
     * 释放，退出时候调用
     */
    public void release(Context context){
        REventsCenter.getInstance(context).release();
    }
}
