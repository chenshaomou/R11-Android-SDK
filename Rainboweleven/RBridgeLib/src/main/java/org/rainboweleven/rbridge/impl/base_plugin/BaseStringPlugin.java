package org.rainboweleven.rbridge.impl.base_plugin;

import org.rainboweleven.rbridge.core.RBridgePlugin;

/**
 * 同步插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-23 13:13 GMT+8
 * @email 411086563@qq.com
 */
public abstract class BaseStringPlugin implements RBridgePlugin<String, String> {

    @Override
    public abstract String onPluginCalled(String module, String method, String params);
}
