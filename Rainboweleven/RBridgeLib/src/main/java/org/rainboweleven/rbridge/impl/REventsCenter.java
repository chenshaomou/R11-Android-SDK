package org.rainboweleven.rbridge.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件中心，接收所有事件
 *
 * @author andy(Andy)
 * @datetime 2018-03-24 17:05 GMT+8
 * @email 411086563@qq.com
 */
public class REventsCenter extends BroadcastReceiver {

    // 原生用户发出的事件
    private static final String ACTION_USER_SEND = "org.rainboweleven.rbridge.impl.REventsCenter.ACTION_USER_SEND";

    // H5发来的事件
    private static final String ACTION_H5_SEND = "org.rainboweleven.rbridge.impl.REventsCenter.ACTION_H5_SEND";

    /**
     * WebView接口
     */
    private List<RWebViewInterface> mRWebViewInterfaces = new ArrayList<>();
    /**
     * 所有事件的观察者
     */
    private Map<String, List<EventObserver>> mEventObserversMap = new HashMap<>();
    // Context
    private Context mContext;
    // 单例
    private static REventsCenter sInstance;

    /**
     * 获取单例
     *
     * @param context
     * @return
     */
    public static REventsCenter getInstance(Context context) {
        if (sInstance == null) {
            synchronized (REventsCenter.class) {
                if (sInstance == null) {
                    sInstance = new REventsCenter(context);
                }
            }
        }
        return sInstance;
    }

    // 构造方法
    private REventsCenter(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * WebView已经初始化好了
     */
    public void onRWebViewReady(RWebViewInterface rWebViewInterface) {
        if (rWebViewInterface == null) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USER_SEND);// 原生用户发出的事件
        filter.addAction(ACTION_H5_SEND);// H5发来的事件

        // 其他需要监听的系统事件
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);// 如：SD卡卸载

        rWebViewInterface.context().registerReceiver(this, filter);
        mRWebViewInterfaces.add(rWebViewInterface);
    }

    /**
     * WebView未初始化
     */
    public void onRWebViewNotReady(RWebViewInterface rWebViewInterface) {
        if (rWebViewInterface == null) {
            return;
        }
        rWebViewInterface.context().unregisterReceiver(this);
        mRWebViewInterfaces.remove(rWebViewInterface);
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        // 接收到所有事件，分发处理，事件分为几种：
        // 1、原生系统事件，只需要在广播注册的时候加上要监听事件即可，这个事件需要转发给所有on注册的观察者以及发往H5的事件处理入口RWebViewInterface.CALL_JS_BRIDGE_EVENT_TIGGER
        // 2、原生用户发出的事件，调用send方法发送后即可在onReceive收到，同理：这个事件需要转发给所有on注册的观察者以及发往H5的事件处理入口RWebViewInterface
        // .CALL_JS_BRIDGE_EVENT_TIGGER
        // 3、H5发来的事件，这个事件是H5端通过EventsPlugin分发下来的，在EventsPlugin调用sendByH5方法发送后即可在onReceive收到，这个事件需要转发给所有on注册的观察者即可

        String action = intent.getAction();

        String eventName = intent.getStringExtra("eventName");
        String params = intent.getStringExtra("params");

        if (TextUtils.isEmpty(eventName)) {
            return;
        }

        // 原生用户发出的事件
        if (ACTION_USER_SEND.equals(action)) {
            handleUserEvent(eventName, params);
        }
        // H5发来的事件
        else if (ACTION_H5_SEND.equals(action)) {
            handleH5Event(eventName, params);
        }
        // 原生系统事件
        else {
            eventName = intent.getAction();// 暂定action为eventName
            params = intent.getDataString();// 暂定data为params
            handleSystemEvent(eventName, params);
        }
    }

    // 处理原生系统事件
    private void handleSystemEvent(String eventName, String params) {
        handleUserEvent(eventName, params);
    }

    // 处理原生用户发出的事件
    private void handleUserEvent(String eventName, String params) {
        // 转发给所有on注册的观察者
        List<EventObserver> observers = mEventObserversMap.get(eventName);
        if (observers != null) {
            for (EventObserver observer : observers) {
                if (observer == null) {
                    continue;
                }
                observer.onObserver(eventName, params);
            }
        }
        // 发往H5的事件处理入口RWebViewInterface.CALL_JS_BRIDGE_EVENT_TIGGER
        for (RWebViewInterface viewInterface : mRWebViewInterfaces) {
            if (viewInterface == null) {
                continue;
            }
            String script = String.format(RWebViewInterface.CALL_JS_BRIDGE_EVENT_TIGGER, eventName, params);
            viewInterface.evaluateJavascript(script, null);
        }
    }

    // 处理H5发来的事件
    private void handleH5Event(String eventName, String params) {
        // 转发给所有on注册的观察者
        List<EventObserver> observers = mEventObserversMap.get(eventName);
        if (observers != null) {
            for (EventObserver observer : observers) {
                if (observer == null) {
                    continue;
                }
                observer.onObserver(eventName, params);
            }
        }
    }

    /**
     * 向整个系统发送一个事件
     *
     * @param eventName
     * @param params
     */
    public void send(String eventName, String params) {
        Intent intent = new Intent();
        intent.setAction(ACTION_USER_SEND);
        intent.putExtra("eventName", eventName);
        intent.putExtra("params", params);
        mContext.sendBroadcast(intent);
    }

    /**
     * 向整个系统发送一个事件
     *
     * @param eventName
     * @param params
     */
    public void sendByH5(String eventName, String params) {
        Intent intent = new Intent();
        intent.setAction(ACTION_H5_SEND);
        intent.putExtra("eventName", eventName);
        intent.putExtra("params", params);
        mContext.sendBroadcast(intent);
    }

    /**
     * 添加事件观察者
     *
     * @param eventName
     * @param observer
     */
    public void on(String eventName, EventObserver observer) {
        List<EventObserver> observers = mEventObserversMap.get(eventName);
        if (observers == null) {
            observers = new ArrayList<>();
            mEventObserversMap.put(eventName, observers);
        }
        observers.add(observer);
    }

    /**
     * 移除事件观察者
     *
     * @param eventName
     * @param observer
     */
    public void off(String eventName, EventObserver observer) {
        List<EventObserver> observers = mEventObserversMap.get(eventName);
        if (observers != null) {
            observers.remove(observer);
        }
    }
}
