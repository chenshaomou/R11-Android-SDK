package org.rainboweleven.rbridge.impl.string_plugin;

import android.util.Log;

import org.rainboweleven.rbridge.impl.base_plugin.BaseStringAsyncPlugin;

/**
 * 网络插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class NetworkPlugin extends BaseStringAsyncPlugin {

    @Override
    public void onPluginCalled(String module, String method, String params, OnCallPluginListener<String> listener) {
        String result = "NetworkPlugin(StringPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("wlf", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listener != null) {
            listener.onCallPluginResult(result);
            listener.onCallPluginFinish();
        }
    }
}
