package org.rainboweleven.rbridge.core;

/**
 * WebView操作html操作接口
 *
 * @author andy(Andy)
 * @datetime 2017-12-11 10:39 GMT+8
 * @email 411086563@qq.com
 */
public interface RWebViewInterface<JSRESULT> {

    /**
     * 模块为空的时候默认挂载到userDefault对象
     */
    String MODULE_DEFAULT = "userDefault";
    /**
     * 调用jsBridge.callbacks中的回调方法
     */
    String CALL_JS_BRIDGE_CALLBACK = "javascript:window.jsBridge.callbacks.%s(%s)";
    /**
     * 删除异步jsBridge.callbacks中的回调方法
     */
    String DELETE_JS_BRIDGE_CALLBACK = "javascript:delete window.jsBridge.callbacks.%s";
    /**
     * 调用jsBridge中的模块方法
     */
    String CALL_JS_BRIDGE_MODULE_FUNCTION = "javascript:window.jsBridge.%s.%s.apply(window.jsBridge.%s, %s)";
    /**
     * 在jsBridge中创建插件
     */
    String CREATE_PLUGIN_IN_JS_BRIDGE = "javascript:if(undefined===jsBridge.%s){jsBridge.%s={}}jsBridge.%s.%s=function(params,callback){if(arguments.length===0){return getJsBridge().call(%s,%s,{})}if(arguments.length===1){return getJsBridge().call(%s,%s,params)}if(arguments.length===2){return getJsBridge().call(%s,%s,params,callback)}};";

    /**
     * 读取本地页面
     *
     * @param url
     * @param hash
     */
    void loadLocalURL(String url, String hash);

    /**
     * 读取远程页面
     *
     * @param url
     * @param hash
     */
    void loadRemoteURL(String url, String hash);

    /**
     * 执行JavaScript
     *
     * @param script
     * @param listener
     */
    void evaluateJavascript(String script, OnCallJsResultListener<JSRESULT> listener);

    /**
     * 注册插件
     *
     * @param module
     * @param method
     * @param plugin
     */
    void register(String module, String method, RBridgePlugin plugin);

    /**
     * 注册异步插件
     *
     * @param module
     * @param method
     * @param plugin
     */
    void register(String module, String method, RBridgeAsyncPlugin plugin);

    /**
     * 调用JS结果监听器
     */
    interface OnCallJsResultListener<RESULT> {

        /**
         * 调用JS结果
         *
         * @param result JS返回的结果
         */
        void onCallJsResult(RESULT result);
    }

    /**
     * 事件监听器
     */
    interface OnEventReceivedListener<PARAMS> {
        /**
         * 监听到事件
         *
         * @param module
         * @param method
         * @param params
         */
        void onEventReceived(String module, String method, PARAMS params);
    }
}
