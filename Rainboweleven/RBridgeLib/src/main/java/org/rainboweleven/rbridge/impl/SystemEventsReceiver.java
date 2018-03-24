package org.rainboweleven.rbridge.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.rainboweleven.rbridge.core.RWebViewInterface;

/**
 * 系统事件监听器
 *
 * @author andy(Andy)
 * @datetime 2018-03-24 17:05 GMT+8
 * @email 411086563@qq.com
 */
public class SystemEventsReceiver extends BroadcastReceiver {

    /**
     * WebView操作html操作接口
     */
    private RWebViewInterface mWebViewInterface;

    public SystemEventsReceiver(RWebViewInterface webViewInterface) {
        mWebViewInterface = webViewInterface;
    }

    /**
     * WebView已经初始化好了
     */
    public void onRWebViewReady() {
        mWebViewInterface.context().registerReceiver(this, null);
    }

    /**
     * WebView未初始化
     */
    public void onRWebViewNotReady() {
        mWebViewInterface.context().unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // action为事件名
        String eventName = intent.getAction();
        // FIXME 系统事件参数需要根据不同的action进行不同的逻辑得到
        String params = intent.getDataString();
        mWebViewInterface.send(eventName, params);
    }
}
