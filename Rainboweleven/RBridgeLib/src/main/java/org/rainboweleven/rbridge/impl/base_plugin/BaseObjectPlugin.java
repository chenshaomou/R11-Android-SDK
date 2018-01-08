package org.rainboweleven.rbridge.impl.base_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgePlugin;
import org.rainboweleven.rbridge.impl.PluginResult;
import org.rainboweleven.rbridge.util.CreateHtmlPluginUtil;

/**
 * 同步插件，传递JSONObject参数，返回PluginResult结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-23 13:15 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseObjectPlugin implements RBridgePlugin<JSONObject, PluginResult> {

    // 用于反射取泛型类型值
    private RBridgePlugin<JSONObject, PluginResult> mSelf = this;

    @Override
    public abstract PluginResult onPluginCalled(String module, String method, JSONObject params);

    @Override
    public String onGetCreatePluginScript(String module, String method) {
        return CreateHtmlPluginUtil.getCreatePluginScript(module, method, null);
    }
}