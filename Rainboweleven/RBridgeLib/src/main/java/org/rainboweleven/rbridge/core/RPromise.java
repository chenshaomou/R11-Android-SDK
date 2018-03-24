package org.rainboweleven.rbridge.core;

/**
 * Created by chenshaomou on 24/03/2018.
 */

public class RPromise {

    private String result = null;
    private OnCallPluginListener listener = null;

    public OnCallPluginListener getListener() {
        return listener;
    }

    public void setListener(OnCallPluginListener listener) {
        this.listener = listener;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if (this.listener != null){
            listener.onCallPluginResult(result);
        }
        this.result = result;
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
