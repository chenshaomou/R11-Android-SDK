package org.rainboweleven.rbridge.impl.plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenshaomou on 24/03/2018.
 */
public class EventsPlugin extends RWebkitPlugin {

    public static final String MODULE_NAME = "events";
    public static final String SEND_EVENT = "send";

    private Map<String, List<EventObserver>> mEventObserversMap;

    public EventsPlugin() {
        mEventObserversMap = new HashMap<>();
    }

    @Override
    public void onPluginCalled(String module, String method, String params, RPromise promise) {
        if (!MODULE_NAME.equals(module)) {
            return;
        }
        // 发送事件
        if (SEND_EVENT.equals(method)) {
            try {
                JSONObject jsonParams = new JSONObject(params);
                String eventName = jsonParams.optString("eventName");
                String eventParams = jsonParams.optString("params");

                List<EventObserver> observers = mEventObserversMap.get(eventName);
                if (observers != null) {
                    for (EventObserver observer : observers) {
                        if (observer == null) {
                            continue;
                        }
                        observer.onObserver(eventName, eventParams);
                    }
                    promise.setResult("true");// 事件发送成功
                }
            } catch (Exception e) {
                e.printStackTrace();
                promise.setResult(e.getMessage());// 事件发送失败
            }
        }
    }

    /**
     * 添加观察者
     *
     * @param eventName
     * @param observer
     */
    public void addObserver(String eventName, EventObserver observer) {
        List<EventObserver> observers = mEventObserversMap.get(eventName);
        if (observers == null) {
            observers = new ArrayList<>();
            mEventObserversMap.put(eventName, observers);
        }
        observers.add(observer);
    }

    /**
     * 移除观察者
     *
     * @param eventName
     * @param observer
     */
    public void removeObserver(String eventName, EventObserver observer) {
        List<EventObserver> observers = mEventObserversMap.get(eventName);
        if (observers != null) {
            observers.remove(observer);
        }
    }

}
