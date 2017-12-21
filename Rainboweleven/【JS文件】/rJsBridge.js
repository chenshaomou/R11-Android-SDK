/**
 * 获取JsBridge API object
 * @returns {{call: call, on: on, off: off, send: send, register: register}}
 */
function getJsBridge() {

    // window下定义jsBridge
    window.jsBridge = window.jsBridge || {}
    // jsBridge下定义callbacks，异步调用插件时的回调function集合
    window.jsBridge.callbacks = window.jsBridge.callbacks || []
    // jsBridge下定义callbackCount，异步调用插件时的回调function命名计算标识
    window.jsBridge.callbackCount = window.jsBridge.callbackCount || 0
    // jsBridge下定义原生WebView类型信息，由原生WebView初始化的时候写入到window.jsBridge.webViewType中，取值：
    // UIWV：ios UIWebView（ios 8.0以下）
    // WKWV：ios WKWebView（ios 8.0及以上）
    // ADWKWV：android WebKit WebView（android 4.4以下）
    // ADCRMWV：android chromium WebView（android 4.4及以上）
    window.jsBridge.webViewType = window.jsBridge.webViewType || ''

    return {
        /**
         * 注册插件/方法到window.jsBridge
         * @param module 模块名
         * @param method 方法名
         * @param callFun 方法体
         */
        register: function (module, method, callFun) {
            // 注册module和method
            if (typeof module == 'object') {
                // 未注册module，先注册
                if (!window.jsBridge.module) {
                    Object.assign(window.jsBridge, module)
                }
                // 注册function到module下
                window.jsBridge.module[method] = callFun
            }
            // 没有module，直接注册到window.jsBridge下
            else if (typeof module == 'undefined') {
                window.jsBridge[method] = callFun
            } else {
                // 异常，其他类型暂不支持注册，FIXME H5可以考虑自己吞了通用异常逻辑
            }
        },
        /**
         * 调用原生的核心方法，调用原生插件的同步/异步方法
         * @param module 模块名
         * @param method 方法名
         * @param params 参数
         * @param callback 异步调用时完成的回调function
         * @returns 同步时返回string，异步返回void
         * 同步和异步返回的数据结构：
         * {
         *     isError: true,
         *     error: {
         *         msg: '参数为空异常',
         *         code: '002'
         *     },
         *     data: {}
         * }
         */
        call: function (module, method, params, callback) {
            // 1、异步
            if (typeof callback == 'function') {
                // 回调的function名字，默认挂在window.jsBridge.callbacks下面，同时会传递给原生作为原生回调的callback引用名字，例如store_getValue_10
                var callbackName = module + '_' + method + '_' + window.jsBridge.callbackCount++
                // 将callbackName挂到window.jsBridge.callbacks下面
                window.jsBridge.callbacks[callbackName] = callback
                // 请求原生的所有数据
                var request = {
                    'module': module,
                    'method': method,
                    'params': params,
                    'callbackName': callbackName
                }
                // 将请求数据转成字符串
                var requestStr = JSON.stringify(request || {})
                // ios WKWebView
                if (window.jsBridge.webViewType == 'WKWV') {
                    // 执行window.prompt进行插件调用
                    window.prompt('', requestStr)
                }
                // 其他WebView
                else {
                    // window.nativeBridge是由原生WebView API注册生成，各个平台具体实现可能不一样，android一般是个object，ios一般是个function
                    // 如果window.nativeBridge是一个function，那么执行nativeBridge方法进行插件调用，如果ios UIWebView
                    if (typeof window.nativeBridge == 'function') {
                        window.nativeBridge(requestStr)
                    }
                    // 如果nativeBridge是一个object，那么执行nativeBridge的call方法进行插件调用，如果android WebView
                    else if (typeof window.nativeBridge == 'object') {
                        window.nativeBridge.call(requestStr)
                    }
                    // 没有定义nativeBridge对象或者nativeBridge为其他类型，暂时不支持
                    else {
                        // 没有可以调用的原生bridge对象，FIXME H5可以考虑自己吞了通用异常逻辑
                    }
                }
            }
            // 2、同步
            else {
                // 请求原生的所有数据
                var request = {
                    'module': module,
                    'method': method,
                    'params': params
                }
                // 将请求数据转成字符串
                var requestStr = JSON.stringify(request || {})
                // ios WKWebView
                if (window.jsBridge.webViewType == 'WKWV') {
                    // 执行window.prompt进行插件调用
                    return window.prompt('', requestStr);
                }
                // 其他WebView
                else {
                    // window.nativeBridge是由原生WebView API注册生成，各个平台具体实现可能不一样，android一般是个object，ios一般是个function
                    // 如果nativeBridge是一个function，那么执行nativeBridge方法进行插件调用，如果ios UIWebView
                    if (typeof window.nativeBridge == 'function') {
                        return window.nativeBridge(requestStr)
                    }
                    // 如果nativeBridge是一个object，那么执行nativeBridge的call方法进行插件调用，如果android WebView
                    else if (typeof window.nativeBridge == 'object') {
                        return window.nativeBridge.call(requestStr)
                    }
                    // 没有定义nativeBridge对象或者nativeBridge为其他类型，暂时不支持
                    else {
                        // 没有可以调用的原生对象，FIXME H5可以考虑自己吞了通用异常逻辑
                    }
                }
            }
        },
        /**
         * 异步调用原生的插件（基于promise）
         * @param module 模块名
         * @param method 方法名
         * @param params 参数
         * @returns {Promise}
         */
        promise: function (module, method, params) {
            return new Promise(function (resolve, reject) {
                this.call(module, method, params, function (result) {
                    if (!result.isError) {
                        resolve(result.data)
                    } else {
                        reject(result.error)
                    }
                })
            })
        },
        /**
         * 监听原生事件
         * @param eventName 要监听的事件名
         * @param callback 回调方法
         */
        on: function (eventName, callback) {
            // 监听事件，文档规定监听原生事件的module值固定为：event
            this.call('event', 'on', {'eventName': eventName}, callback)
        },
        /**
         * 解除监听原生事件jsBridge
         * @param eventName 取消监听的事件名
         */
        off: function (eventName) {
            // 解除监听，文档规定解除监听原生事件的module值固定为：event
            this.call('event', 'off', {'eventName': eventName})
        },
        /**
         * 发送事件到原生
         * @param eventName 要发送的事件名
         * @param params 参数
         */
        send: function (eventName, params) {
            // 发送事件，文档规定解除监听原生事件的module值固定为：event
            this.call('event', 'send', {'eventName': eventName, "params": params})
        }
    }
}

module.exports = getJsBridge()