package org.rainboweleven.rbridge.impl.base_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.impl.PluginResult;

/**
 * 异步插件，传递JSONObject参数，返回PluginResult结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-23 13:13 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseObjectAsyncPlugin implements RBridgeAsyncPlugin<JSONObject, PluginResult> {

    @Override
    public abstract void onPluginCalled(String module, String method, JSONObject params,
                                        OnCallPluginListener<PluginResult> listener);
}
