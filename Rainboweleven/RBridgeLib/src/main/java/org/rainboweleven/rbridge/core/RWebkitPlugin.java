package org.rainboweleven.rbridge.core;

/**
 * 异步插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-23 13:13 GMT+8
 * @email 411086563@qq.com
 */
public abstract class RWebkitPlugin {

    public static String RWEBKIT_PLUGIN_ASYNC_RUNNING = "RWEBKIT_PLUGIN_ASYNC_RUNNING";

    /**
     * 生成js方法代码
     * */
    public static String CREATE_JS_METHOD_FUNCTION = "window.jsBridge['module']=window.jsBridge['module']||{};Object.assign(window.jsBridge['module'],{'method':function(params,callback){if(arguments.length===0){return window.jsBridge.call('module','method',{})}if(arguments.length===1){return window.jsBridge.call('module','method',params)}if(arguments.length===2){window.jsBridge.call('module','method',params,callback)}}});Object.assign(window.jsBridge['module'],{'methodPromise':function(params){if(arguments.length===0){return window.jsBridge.promise('module','method',{})}if(arguments.length===1){return window.jsBridge.promise('module','method',params)}}});";

    /**
     *
     * @param module
     * @param method
     * @param params
     * @return
     */
    public abstract void onPluginCalled(String module, String method, String params,RPromise promise);


    /**
     * 产生js脚步
     * @param module
     * @param method
     * @return
     */
    public String onGetCreatePluginScript(String module, String method) {

        String script = CREATE_JS_METHOD_FUNCTION.replace("module",module).replace("method",method);
        return script;
    }
}
