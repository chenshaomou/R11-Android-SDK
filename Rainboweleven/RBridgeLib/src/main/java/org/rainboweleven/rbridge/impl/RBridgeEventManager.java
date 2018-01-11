package org.rainboweleven.rbridge.impl;

import android.util.Log;

import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin.OnCallPluginListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件管理器
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 09:35 GMT+8
 * @email 411086563@qq.com
 */
public class RBridgeEventManager {

    private static RBridgeEventManager sInstance;

    // 事件回调集合，key为事件名字，value为回调callback
    private Map<String, OnCallPluginListener<PluginResult>> mCallPluginListenerMap = new HashMap<>();

    /**
     * 获取单例
     *
     * @return
     */
    public static RBridgeEventManager getInstance() {
        if (sInstance == null) {
            synchronized (RBridgeEventManager.class) {
                if (sInstance == null) {
                    sInstance = new RBridgeEventManager();
                }
            }
        }
        return sInstance;
    }

    // 构造方法
    private RBridgeEventManager() {
    }

    // 注册事件
    public void register(String eventName, OnCallPluginListener<PluginResult> listener) {
        Log.e("andy", "注册事件,eventName:" + eventName + ",listener:" + listener);
        mCallPluginListenerMap.put(eventName, listener);
    }

    // 取消注册事件
    public void unregister(String eventName) {
        OnCallPluginListener<PluginResult> listener = mCallPluginListenerMap.get(eventName);
        if(listener != null) {
            listener.onCallPluginFinish();
        }
        mCallPluginListenerMap.remove(eventName);

        Log.e("andy", "取消注册事件,eventName:" + eventName + ",listener:" + listener);
    }

    // 发送事件
    public void send(String eventName, Object params) {

        // TODO send logic

        String msg = "发送事件,eventName:" + eventName + ",params:" + params;

        OnCallPluginListener<PluginResult> listener = mCallPluginListenerMap.get(eventName);
        if(listener != null) {
            PluginResult result = new PluginResult(msg);
            listener.onCallPluginResult(result);
        }

        Log.e("andy", "发送事件,eventName:" + eventName + ",listener:" + listener);
    }
}
