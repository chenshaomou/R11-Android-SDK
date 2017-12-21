package org.rainboweleven.rbridge.impl;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;

/**
 * 传递参数为JSONObject类型，返回参数为PluginResult的插件异步插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 14:58 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseAsyncPlugin implements RBridgeAsyncPlugin<JSONObject, PluginResult> {
    @Override
    public abstract void onPluginCalled(String module, String method, JSONObject params,
                                        OnCallPluginListener<PluginResult> listener);
}
