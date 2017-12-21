package org.rainboweleven.rbridge.impl;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgePluginManager;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.impl.core_plugin.EventPlugin;
import org.rainboweleven.rbridge.impl.core_plugin.StorePlugin;

/**
 * 插件管理器，管理传递参数为JSONObject类型，返回参数为PluginResult的插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:31 GMT+8
 * @email 411086563@qq.com
 */
public class RSystemPluginManager extends RBridgePluginManager<JSONObject, PluginResult> {

    // 单例
    private static RSystemPluginManager sInstance;

    /**
     * 获取单例
     *
     * @return
     */
    public static RSystemPluginManager getInstance() {
        if (sInstance == null) {
            synchronized (RSystemPluginManager.class) {
                if (sInstance == null) {
                    sInstance = new RSystemPluginManager();
                }
            }
        }
        return sInstance;
    }

    // 构造方法
    private RSystemPluginManager() {
    }

    // 初始化sdk插件
    public void initPlugins(RWebViewInterface webViewInterface) {
        StorePlugin storePlugin = new StorePlugin();
        register(webViewInterface, "store", "set", storePlugin);
        register(webViewInterface, "store", "get", storePlugin);
        register(webViewInterface, "store", "getAll", storePlugin);
        register(webViewInterface, "store", "remove", storePlugin);
        register(webViewInterface, "store", "removeAll", storePlugin);
    }

    // 初始化sdk事件
    public void initEvents(RWebViewInterface webViewInterface) {
        EventPlugin eventPlugin = new EventPlugin();
        register(webViewInterface, "event", "on", eventPlugin);
        register(webViewInterface, "event", "off", eventPlugin);
        register(webViewInterface, "event", "send", eventPlugin);
    }
}
