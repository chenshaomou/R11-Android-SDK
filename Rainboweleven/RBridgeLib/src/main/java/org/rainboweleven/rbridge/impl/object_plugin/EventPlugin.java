package org.rainboweleven.rbridge.impl.object_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.RBridgeEventManager;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectPlugin;

/**
 * 事件插件，传递JSONObject参数，返回PluginResult结果，只处理同步的send和off
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class EventPlugin extends BaseObjectPlugin {

    @Override
    public PluginResult onPluginCalled(String module, String method, JSONObject params) {

        PluginResult result = new PluginResult(false);

        // 文档规定module只能为event
        if (!"event".equals(module)) {
            return result;
        }

        try {
            String eventName = params.getString("eventName");

            // 取消注册
            if ("off".equals(method)) {
                RBridgeEventManager.getInstance().unregister(eventName);
                result = new PluginResult(true);
            }
            // 发送事件
            else if ("send".equals(method)) {
                JSONObject eventParams = params.optJSONObject("params");
                RBridgeEventManager.getInstance().send(eventName, eventParams);
                result = new PluginResult(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
