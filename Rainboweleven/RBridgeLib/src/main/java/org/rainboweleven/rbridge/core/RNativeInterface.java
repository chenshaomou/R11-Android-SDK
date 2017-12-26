package org.rainboweleven.rbridge.core;

import android.webkit.JavascriptInterface;

/**
 * html调用native插件的接口方法
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:14 GMT+8
 * @email 411086563@qq.com
 */
public interface RNativeInterface {

    /**
     * html调用native插件
     *
     * @param request 请求字符串，文档约定数据结构如：
     *                {
     *                "module": "store",
     *                "method": "getValue",
     *                "params": {
     *                "key": "token"
     *                },
     *                "callbackName": "store_getValue_10"
     *                }
     */
    @JavascriptInterface
    String call(String request);
}
