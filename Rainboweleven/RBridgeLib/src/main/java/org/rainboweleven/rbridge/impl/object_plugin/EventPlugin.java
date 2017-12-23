package org.rainboweleven.rbridge.impl.object_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.RBridgeEventManager;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectAsyncPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件插件，传递JSONObject参数，返回PluginResult结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class EventPlugin extends BaseObjectAsyncPlugin {

    private Map<String, OnCallPluginListener<PluginResult>> mCallPluginListenerMap = new HashMap<>();

    @Override
    public void onPluginCalled(String module, String method, JSONObject params, OnCallPluginListener<PluginResult>
            listener) {
        // 文档规定module只能为event
        if (!"event".equals(module)) {
            return;
        }
        try {
            String eventName = params.getString("eventName");
            JSONObject eventParams = params.getJSONObject("params");
            if ("on".equals(method)) {
                RBridgeEventManager.getInstance().register(eventName, eventParams);
                mCallPluginListenerMap.put(eventName, listener);
            } else if ("off".equals(method)) {
                RBridgeEventManager.getInstance().unregister(eventName);
                OnCallPluginListener pluginListener = mCallPluginListenerMap.get(eventName);
                pluginListener.onCallPluginFinish();
                mCallPluginListenerMap.remove(eventName);
            } else if ("send".equals(method)) {
                RBridgeEventManager.getInstance().send(eventName, eventParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
