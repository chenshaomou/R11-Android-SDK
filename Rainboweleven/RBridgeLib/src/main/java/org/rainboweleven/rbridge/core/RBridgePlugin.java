package org.rainboweleven.rbridge.core;

/**
 * 同步插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-17 18:09 GMT+8
 * @email 411086563@qq.com
 */
public interface RBridgePlugin<PARAMS, RESULT> {
    /**
     * 插件被调用
     *
     * @param module
     * @param method
     * @param params
     * @return
     */
    RESULT onPluginCalled(String module, String method, PARAMS params);
}
