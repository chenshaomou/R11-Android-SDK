package org.rainboweleven.rbridge.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * 插件返回参数
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 11:16 GMT+8
 * @email 411086563@qq.com
 */
public class PluginResult {

    @SerializedName("isError")
    private boolean mIsError;

    @SerializedName("error")
    private Error mError;

    @SerializedName("data")
    private Object mData;

    protected PluginResult() {
    }

    public PluginResult(String errorMsg, String errorCode) {
        mIsError = true;
        mError = new Error(errorMsg, errorCode);
    }

    public PluginResult(Object data) {
        mIsError = false;
        mData = data;
    }

    public PluginResult(boolean isError, String errorMsg, String errorCode, Object data) {
        mIsError = isError;
        mError = new Error(errorMsg, errorCode);
        mData = data;
    }

    // 异常信息
    private static class Error {

        // 异常信息
        @SerializedName("msg")
        private String mMsg;

        // 异常代码
        @SerializedName("code")
        private String mCode;

        public Error(String msg, String code) {
            mMsg = msg;
            mCode = code;
        }
    }

    /**
     * 序列号成JSON字符串
     *
     * @return
     */
    public String toJsonString() {
        return new GsonBuilder().create().toJson(this);
    }
}
