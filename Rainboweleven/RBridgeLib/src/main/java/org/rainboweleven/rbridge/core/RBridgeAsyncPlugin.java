package org.rainboweleven.rbridge.core;

/**
 * 异步插件
 *
 * @param <PARAMS> 参数类型
 * @param <RESULT> 返回结果类型
 * @author andy(Andy)
 * @datetime 2017-12-11 10:39 GMT+8
 * @email 411086563@qq.com
 */
public interface RBridgeAsyncPlugin<PARAMS, RESULT> {

    /**
     * 插件被调用
     *
     * @param module
     * @param method
     * @param params
     * @param listener
     */
    void onPluginCalled(String module, String method, PARAMS params, OnCallPluginListener<RESULT> listener);

    /**
     * 获取挂载插件到html上的JavaScript代码
     *
     * @param module
     * @param method
     * @return
     */
    String onGetCreatePluginScript(String module, String method);

    /**
     * 调用native插件方法监听器
     *
     * @param <RESULT> 返回结果类型
     */
    interface OnCallPluginListener<RESULT> {

        /**
         * 调用native插件方法完成，可以适当的做一些清理工作
         */
        void onCallPluginFinish();

        /**
         * 调用native插件方法传递回调数据
         *
         * @param result
         */
        void onCallPluginResult(RESULT result);
    }
}
