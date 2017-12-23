package org.rainboweleven.rbridge.impl.string_plugin;

import android.util.Log;

import org.rainboweleven.rbridge.impl.base_plugin.BaseStringPlugin;

/**
 * 存储插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-17 21:23 GMT+8
 * @email 411086563@qq.com
 */
public class StorePlugin extends BaseStringPlugin {

    @Override
    public String onPluginCalled(String module, String method, String params) {
        String result = "StorePlugin(StringPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("wlf", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
