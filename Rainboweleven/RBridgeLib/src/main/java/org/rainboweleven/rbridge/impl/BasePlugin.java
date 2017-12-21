package org.rainboweleven.rbridge.impl;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgePlugin;

/**
 * 传递参数为JSONObject类型，返回参数为PluginResult的同步插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 14:58 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BasePlugin implements RBridgePlugin<JSONObject, PluginResult> {
    @Override
    public abstract PluginResult onPluginCalled(String module, String method, JSONObject params);
}
