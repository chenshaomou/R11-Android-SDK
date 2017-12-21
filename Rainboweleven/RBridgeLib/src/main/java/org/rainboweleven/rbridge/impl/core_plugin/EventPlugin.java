package org.rainboweleven.rbridge.impl.core_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.BaseAsyncPlugin;
import org.rainboweleven.rbridge.impl.RSystemEventManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class EventPlugin extends BaseAsyncPlugin {

    private Map<String, OnCallPluginListener> mCallPluginListenerMap = new HashMap<>();

    @Override
    public void onPluginCalled(String module, String method, JSONObject params, OnCallPluginListener listener) {
        if (!"event".equals(module)) {
            return;
        }
        try {
            String eventName = params.getString("eventName");
            JSONObject eventParams = params.getJSONObject("params");
            if ("on".equals(method)) {
                RSystemEventManager.getInstance().register(eventName, eventParams);
                mCallPluginListenerMap.put(eventName, listener);
            } else if ("off".equals(method)) {
                RSystemEventManager.getInstance().unregister(eventName);
                OnCallPluginListener pluginListener = mCallPluginListenerMap.get(eventName);
                pluginListener.onCallPluginFinish();
                mCallPluginListenerMap.remove(eventName);
            } else if ("send".equals(method)) {
                RSystemEventManager.getInstance().send(eventName, eventParams);
            }
        } catch (Exception e) {

        }
    }
}
