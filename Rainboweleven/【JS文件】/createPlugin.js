if (undefined === jsBridge.module) {
    jsBridge.module = {}
}
jsBridge.module.method = function (params, callback) {
    // 参数为0个
    if (arguments.length === 0) {
        return getJsBridge().call(module, method, {})
    }
    // 参数为1个
    if (arguments.length === 1) {
        return getJsBridge().call(module, method, params)
    }
    // 参数为2个
    if (arguments.length === 2) {
        return getJsBridge().call(module, method, params, callback)
    }
}