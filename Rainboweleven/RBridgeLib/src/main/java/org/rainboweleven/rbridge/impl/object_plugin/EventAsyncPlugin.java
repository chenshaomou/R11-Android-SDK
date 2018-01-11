package org.rainboweleven.rbridge.impl.object_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.RBridgeEventManager;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectAsyncPlugin;

/**
 * 事件插件，传递JSONObject参数，返回PluginResult结果，目前只处理异步的on事件
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class EventAsyncPlugin extends BaseObjectAsyncPlugin {

    public static final String MODULE_NAME = "event";
    public static final String METHOD_ON = "on";
    public static final String PARAM_EVENT_NAME = "eventName";

    @Override
    public void onPluginCalled(String module, String method, JSONObject params, OnCallPluginListener<PluginResult>
            listener) {
        if (!MODULE_NAME.equals(module)) {
            return;
        }
        try {
            String eventName = params.getString(PARAM_EVENT_NAME);
            // 注册
            if (METHOD_ON.equals(method)) {
                RBridgeEventManager.getInstance().register(eventName, listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                // 异常
                listener.onCallPluginResult(new PluginResult("异常,e:" + e.getMessage(), ""));
                listener.onCallPluginFinish();
            }
        }
    }
}
