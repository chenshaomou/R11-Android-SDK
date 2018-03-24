package org.rainboweleven.rbridge.core;

/**
 * Created by chenshaomou on 24/03/2018.
 */
public class RPromise {
    /**
     * 执行结果
     */
    private String mResult = null;
    /**
     * 插件监听器，跟H5的callbackName绑定
     */
    private OnCallPluginListener mListener = null;

    public OnCallPluginListener getListener() {
        return mListener;
    }

    public void setListener(OnCallPluginListener listener) {
        mListener = listener;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        if (mListener != null) {
            mListener.onCallPluginResult(result);
        }
        mResult = result;
    }

    /**
     * 调用native插件方法监听器
     */
    public interface OnCallPluginListener {

        /**
         * 调用native插件方法传递回调数据
         *
         * @param result
         */
        void onCallPluginResult(String result);
    }
}
