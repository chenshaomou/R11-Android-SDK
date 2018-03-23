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
    String INIT_SCRIPT = "function initJsBridge(webViewType){window.jsBridge=window.jsBridge||{};window.jsBridge.callbacks=window.jsBridge.callbacks||[];window.jsBridge.callbackCount=window.jsBridge.callbackCount||0;window.jsBridge.webViewType=window.jsBridge.webViewType||webViewType;window.jsBridge.register=window.jsBridge.register||function(module,method,callFun){if(arguments.length==2){callFun=method;method=module;module='userDefault'}else{if(arguments.length==3){}else{throw'register方法必须是2个或者3个参数'}}var action={};action[method]=callFun;window.jsBridge[module]=window.jsBridge[module]||{};Object.assign(window.jsBridge[module],action)};window.jsBridge.registerNative=window.jsBridge.registerNative||function(module,method,customFun){var lastArg=arguments[arguments.length-1];var hasCustomFun=typeof lastArg=='function';if(arguments.length==1){if(hasCustomFun){throw'registerNative不支持1个为function的参数'}else{method=module;module='userDefault'}}else{if(arguments.length==2){if(hasCustomFun){customFun=method;method=module;module='userDefault'}else{}}else{if(arguments.length==3){}else{throw'registerNative方法必须是1~3个参数'}}}var action={};if(hasCustomFun){action[method]=customFun}else{action[method]=function(params,callback){if(arguments.length===0){return window.jsBridge.call(module,method,{})}if(arguments.length===1){return window.jsBridge.call(module,method,params)}if(arguments.length===2){window.jsBridge.call(module,method,params,callback)}}}window.jsBridge[module]=window.jsBridge[module]||{};Object.assign(window.jsBridge[module],action)};window.jsBridge.call=window.jsBridge.call||function(module,method,params,callback){var lastArg=arguments[arguments.length-1];var async=typeof lastArg=='function';if(arguments.length==3){if(async){callback=params;params=method;method=module;module='userDefault'}else{}}else{if(arguments.length==4){}else{throw'register方法必须是3个或者4个参数'}}if(typeof params!='string'){if(typeof params=='object'||Object.prototype.toString.call(params)=='[object Array]'){params=JSON.stringify(params||{})}else{params=params.toString()}}var request;if(async){var callbackName=module+'_'+method+'_'+window.jsBridge.callbackCount++;window.jsBridge.callbacks[callbackName]=callback;request={'module':module,'method':method,'params':params,'callbackName':callbackName}}else{request={'module':module,'method':method,'params':params}}var requestStr=JSON.stringify(request||{});if(window.jsBridge.webViewType=='WKWV'){return window.prompt(window.jsBridge.webViewType,requestStr)}else{if(typeof window.nativeBridge=='function'){return window.nativeBridge(requestStr)}else{if(typeof window.nativeBridge=='object'){return window.nativeBridge.call(requestStr)}else{console.log('无window.nativeBridge被注册')}}}};window.jsBridge.promise=window.jsBridge.promise||function(module,method,params){return new Promise(function(resolve,reject){try{window.jsBridge.call(module,method,params,function(result){resolve(result)})}catch(e){reject(e)}})};window.jsBridge.on=window.jsBridge.on||function(eventName,callback){window.jsBridge.call('event','on',{'eventName':eventName},callback)};window.jsBridge.off=window.jsBridge.off||function(eventName){return window.jsBridge.call('event','off',{'eventName':eventName})};window.jsBridge.send=window.jsBridge.send||function(eventName,params){return window.jsBridge.call('event','send',{'eventName':eventName,'params':params})};window.jsBridge.sendDocumentEvent=window.jsBridge.sendDocumentEvent||function(eventName,cancelable){var event=window.document.createEvent('Event');if(arguments.length==1||typeof cancelable=='undefined'){cancelable=false}event.initEvent(eventName,false,cancelable);window.document.dispatchEvent(event)}}initJsBridge('%s');";
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
    String CALL_JS_BRIDGE_MODULE_FUNCTION = "javascript:window.jsBridge.%s.%s('%s');";

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
     * 同步执行JavaScript
     *
     * @param script
     */
    String evaluateJavascript(String script);

    /**
     * 异步执行JavaScript
     *
     * @param script
     * @param listener
     */
    void evaluateJavascript(String script, OnCallJsResultListener<String> listener);

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
     * 调用JS结果监听器
     *
     * @param <JSRESULT> 执行JS返回的结果
     */
    interface OnCallJsResultListener<JSRESULT> {

        /**
         * 调用JS结果
         *
         * @param result JS返回的结果
         */
        void onCallJsResult(JSRESULT result);
    }

    /**
     * 事件监听器
     *
     * @param <PARAMS> 传递的事件
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
