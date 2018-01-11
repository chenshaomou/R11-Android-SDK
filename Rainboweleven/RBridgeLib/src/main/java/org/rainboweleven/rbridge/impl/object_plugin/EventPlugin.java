package org.rainboweleven.rbridge.impl.object_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.RBridgeEventManager;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectPlugin;

/**
 * 事件插件，传递JSONObject参数，返回PluginResult结果，只处理同步的send和off事件
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class EventPlugin extends BaseObjectPlugin {

    public static final String MODULE_NAME = "event";
    public static final String METHOD_OFF = "off";
    public static final String METHOD_SEND = "send";
    public static final String PARAM_EVENT_NAME = "eventName";
    public static final String PARAM_EVENT_PARAMS = "params";

    @Override
    public PluginResult onPluginCalled(String module, String method, JSONObject params) {
        // 返回结果
        PluginResult result = null;
        if (!MODULE_NAME.equals(module)) {
            result = new PluginResult("事件模块必须为:" + MODULE_NAME, "");
            return result;
        }
        try {
            String eventName = params.getString(PARAM_EVENT_NAME);
            // 取消注册
            if (METHOD_OFF.equals(method)) {
                RBridgeEventManager.getInstance().unregister(eventName);
                result = new PluginResult(new Object());
            }
            // 发送事件
            else if (METHOD_SEND.equals(method)) {
                JSONObject eventParams = params.optJSONObject(PARAM_EVENT_PARAMS);
                RBridgeEventManager.getInstance().send(eventName, eventParams);
                result = new PluginResult(new Object());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new PluginResult("异常,e:" + e.getMessage(), "");
        }
        return result;
    }
}
