package org.rainboweleven.rbridge.core;

/**
 * 同步插件
 *
 * @param <PARAMS> 参数类型
 * @param <RESULT> 返回结果类型
 * @author andy(Andy)
 * @datetime 2017-12-11 10:39 GMT+8
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

    /**
     * 获取挂载插件到html上的JavaScript代码
     *
     * @param module
     * @param method
     * @return
     */
    String onGetCreatePluginScript(String module, String method);
}
