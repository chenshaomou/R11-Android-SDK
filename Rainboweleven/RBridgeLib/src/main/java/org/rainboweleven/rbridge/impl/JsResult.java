package org.rainboweleven.rbridge.impl;

import com.google.gson.GsonBuilder;

/**
 * Js执行返回的结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 15:36 GMT+8
 * @email 411086563@qq.com
 */
public class JsResult extends PluginResult {

    /**
     * 从JSON字符串构建
     *
     * @param jsonResult
     * @return
     */
    public static JsResult fromJsonStr(String jsonResult) {
        return new GsonBuilder().create().fromJson(jsonResult, JsResult.class);
    }
}
