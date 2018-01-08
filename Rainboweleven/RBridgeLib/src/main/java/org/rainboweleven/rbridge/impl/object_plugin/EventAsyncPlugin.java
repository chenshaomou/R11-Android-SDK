package org.rainboweleven.rbridge.impl.object_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.RBridgeEventManager;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectAsyncPlugin;

/**
 * 事件插件，传递JSONObject参数，返回PluginResult结果，只处理异步的on
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class EventAsyncPlugin extends BaseObjectAsyncPlugin {

    @Override
    public void onPluginCalled(String module, String method, JSONObject params, OnCallPluginListener<PluginResult>
            listener) {
        // 文档规定module只能为event
        if (!"event".equals(module)) {
            return;
        }
        try {
            String eventName = params.getString("eventName");
            // 注册
            if ("on".equals(method)) {
                RBridgeEventManager.getInstance().register(eventName, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
