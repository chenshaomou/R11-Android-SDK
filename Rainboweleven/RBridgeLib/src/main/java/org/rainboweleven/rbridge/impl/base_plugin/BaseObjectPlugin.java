package org.rainboweleven.rbridge.impl.base_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgePlugin;
import org.rainboweleven.rbridge.impl.PluginResult;

/**
 * 同步插件，传递JSONObject参数，返回PluginResult结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-23 13:15 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseObjectPlugin implements RBridgePlugin<JSONObject, PluginResult> {

    @Override
    public abstract PluginResult onPluginCalled(String module, String method, JSONObject params);
}