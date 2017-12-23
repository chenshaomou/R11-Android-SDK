package org.rainboweleven.rbridge.impl.object_plugin;

import android.util.Log;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.impl.base_plugin.BaseObjectPlugin;

/**
 * 存储插件，传递JSONObject参数，返回PluginResult结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-17 21:23 GMT+8
 * @email 411086563@qq.com
 */
public class StorePlugin extends BaseObjectPlugin {

    @Override
    public PluginResult onPluginCalled(String module, String method, JSONObject params) {
        String result = "StorePlugin(ObjectPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("wlf", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PluginResult(result);
    }
}
