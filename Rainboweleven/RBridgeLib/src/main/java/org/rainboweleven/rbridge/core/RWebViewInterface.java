package org.rainboweleven.rbridge.core;

import android.content.Context;

/**
 * WebView操作html操作接口
 *
 * @author andy(Andy)
 * @datetime 2017-12-11 10:39 GMT+8
 * @email 411086563@qq.com
 */
public interface RWebViewInterface {

    /**
     * 初始化脚本
     */
    String INIT_SCRIPT = "function initJsBridge(webViewType){window.jsBridge=window.jsBridge||{};window.jsBridge" +
            ".callbacks=window.jsBridge.callbacks||[];window.jsBridge.callbackCount=window.jsBridge.callbackCount||0;" +
            "window.jsBridge.webViewType=window.jsBridge.webViewType||webViewType;window.jsBridge.register=window" +
            ".jsBridge.register||function(method,callFun){if(arguments.length<2){throw\"register method must be 2  " +
            "params\"}var action={};action[method]=callFun;window.jsBridge.func=window.jsBridge.func||{};Object" +
            ".assign(window.jsBridge.func,action)};window.jsBridge.call=window.jsBridge.call||function(module,method," +
            "params,callback){var lastArg=arguments[arguments.length-1];var async=typeof lastArg==\"function\";if" +
            "(arguments.length==3){if(async){callback=params;params=\"\"}else{}}else{if(arguments.length==4)" +
            "{}else{throw\"register method must be 3 or 4 params\"}}if(typeof params!=\"string\"){if(typeof " +
            "params==\"object\"||Object.prototype.toString.call(params)==\"[object Array]\"){params=JSON.stringify" +
            "(params||{})}else{params=params.toString()}}var request;if(async){var " +
            "callbackName=module+\"_\"+method+\"_\"+window.jsBridge.callbackCount++;window.jsBridge" +
            ".callbacks[callbackName]=callback;request={\"module\":module,\"method\":method,\"params\":params," +
            "\"callbackName\":callbackName}}else{request={\"module\":module,\"method\":method,\"params\":params}}var " +
            "requestStr=JSON.stringify(request||{});if(window.jsBridge.webViewType==\"WKWV\"){if(async){window.prompt" +
            "(window.jsBridge.webViewType,requestStr)}else{return window.prompt(window.jsBridge.webViewType," +
            "requestStr)}}else{if(typeof window.nativeBridge==\"function\"){if(async){window.nativeBridge(requestStr)" +
            "}else{return window.nativeBridge(requestStr)}}else{if(typeof window.nativeBridge==\"object\"){if(async)" +
            "{window.nativeBridge.call(requestStr)}else{return window.nativeBridge.call(requestStr)}}else{console.log" +
            "(\"no window.nativeBridge has been registered\")}}}};window.jsBridge.promise=window.jsBridge" +
            ".promise||function(module,method,params){return new Promise(function(resolve,reject){try{window.jsBridge" +
            ".call(module,method,params,function(result){resolve(result)})}catch(e){reject(e)}})};window.jsBridge" +
            ".on=window.jsBridge.on||function(eventName,observerKey,callback){var lastArg=arguments[arguments" +
            ".length-1];window.jsBridge.events=window.jsBridge.events||{};window.jsBridge.events.observers=window" +
            ".jsBridge.events.observers||{};if(typeof lastArg==\"function\"){if(arguments.length<3)" +
            "{callback=observerKey;observerKey=\"window.jsBridge\"}window.jsBridge.events.observers[eventName]=window" +
            ".jsBridge.events.observers[eventName]||{};window.jsBridge.events" +
            ".observers[eventName][observerKey]=callback}else{throw\"callback must de a function\"}};window.jsBridge" +
            ".off=window.jsBridge.off||function(eventName,observerKey){window.jsBridge.events=window.jsBridge" +
            ".events||{};window.jsBridge.events.observers=window.jsBridge.events.observers||{};if(arguments.length<2)" +
            "{observerKey=\"window.jsBridge\"}if(window.jsBridge.events.observers[eventName]&&window.jsBridge.events" +
            ".observers[eventName][observerKey]){delete window.jsBridge.events.observers[eventName][observerKey]}};" +
            "window.jsBridge.send=window.jsBridge.send||function(eventName,params){params=params||{};params" +
            ".webviewid=window.jsBridge.id;return window.jsBridge.call(\"events\",\"send\",{\"eventName\":eventName," +
            "\"params\":params})};window.jsBridge.sendDocumentEvent=window.jsBridge.sendDocumentEvent||function" +
            "(eventName,cancelable){var event=window.document.createEvent(\"Event\");if(arguments.length==1||typeof " +
            "cancelable==\"undefined\"){cancelable=false}event.initEvent(eventName,false,cancelable);window.document" +
            ".dispatchEvent(event)};window.jsBridge.events=window.jsBridge.events||{};window.jsBridge.events" +
            ".observers=window.jsBridge.events.observers||{};window.jsBridge.events.tigger=window.jsBridge.events" +
            ".tigger||function(eventName,params){if(window.jsBridge.events.observers[eventName]){Object.keys(window" +
            ".jsBridge.events.observers[eventName]).every(function(element,index,array){window.jsBridge.events" +
            ".observers[eventName][element](params)})}}}initJsBridge(\"ADCRMWV\");window.jsBridge.id=\"1\";jsBridge" +
            ".send(\"domLoadFinish\");";
    /**
     * 模块为空的时候默认挂载到userDefault对象
     */
    String MODULE_DEFAULT = "userDefault";
    /**
     * 调用jsBridge.callbacks中的回调方法
     */
    String CALL_JS_BRIDGE_CALLBACK = "window.jsBridge.callbacks.%s('%s');";
    /**
     * 删除异步jsBridge.callbacks中的回调方法
     */
    String DELETE_JS_BRIDGE_CALLBACK = "delete window.jsBridge.callbacks.%s;";
    /**
     * 调用jsBridge中的模块方法
     */
    String CALL_JS_BRIDGE_MODULE_FUNCTION = "javascript:window.jsBridge.func.%s('%s');";
    /**
     * 原生事件发给JS入口方法
     */
    String CALL_JS_BRIDGE_EVENT_TIGGER = "javascript:window.jsBridge.events.tigger('%s','%s');";
    /**
     * 调用jsBridge.sendDocumentEvent方法发送文档事件
     */
    String CALL_SEND_DOCUMENT_EVENT = "javascript:window.jsBridge.sendDocumentEvent('%s')";

    /**
     * 加载本地页面
     *
     * @param url
     * @param hash
     */
    void loadLocalURL(String url, String hash);

    /**
     * 加载远程页面
     *
     * @param url
     * @param hash
     */
    void loadRemoteURL(String url, String hash);

    /**
     * 异步执行JavaScript代码片段
     *
     * @param script
     * @param listener
     */
    void evaluateJavascript(String script, OnCallJsResultListener listener);

    /**
     * 注册插件
     *
     * @param module
     * @param method
     * @param plugin
     */
    void register(String module, String method, RWebkitPlugin plugin);

    /**
     * Context
     *
     * @return
     */
    Context context();

    /**
     * 执行JS插件(方法)结果监听器
     */
    interface OnCallJsResultListener {

        /**
         * 调用JS结果
         *
         * @param result JS返回的结果
         */
        void onCallJsResult(String result);
    }

    /**
     * 事件观察者
     */
    interface EventObserver {
        /**
         * 观察到事件
         *
         * @param eventName
         * @param params
         */
        void onObserver(String eventName, String params);
    }
}
