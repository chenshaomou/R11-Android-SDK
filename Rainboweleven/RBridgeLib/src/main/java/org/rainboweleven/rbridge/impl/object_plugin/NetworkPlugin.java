package org.rainboweleven.rbridge.impl.object_plugin;

import android.util.Log;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectAsyncPlugin;

/**
 * 网络插件，传递JSONObject参数，返回PluginResult结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class NetworkPlugin extends BaseObjectAsyncPlugin {

    @Override
    public void onPluginCalled(String module, String method, JSONObject params, OnCallPluginListener<PluginResult>
            listener) {
        String result = "NetworkPlugin(ObjectPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("wlf", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listener != null) {
            listener.onCallPluginResult(new PluginResult(result));
            listener.onCallPluginFinish();
        }
    }
}
