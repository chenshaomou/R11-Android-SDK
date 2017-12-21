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
     * @param module
     * @param method
     * @param params
     * @return
     */
    @JavascriptInterface
    String call(String module, String method, String params, String jsCallback);
}
