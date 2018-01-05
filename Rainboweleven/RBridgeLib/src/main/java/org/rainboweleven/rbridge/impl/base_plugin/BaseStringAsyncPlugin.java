package org.rainboweleven.rbridge.impl.base_plugin;

import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.util.CreateHtmlPluginUtil;

/**
 * 异步插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-23 13:13 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseStringAsyncPlugin implements RBridgeAsyncPlugin<String, String> {

    @Override
    public abstract void onPluginCalled(String module, String method, String params, OnCallPluginListener<String>
            listener);

    @Override
    public String onGetCreatePluginScript(String module, String method) {
        return CreateHtmlPluginUtil.getCreatePluginScript(module, method, null);
    }
}
