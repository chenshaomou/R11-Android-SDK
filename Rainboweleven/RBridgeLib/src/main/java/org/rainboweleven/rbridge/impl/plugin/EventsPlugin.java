package org.rainboweleven.rbridge.impl.plugin;

import android.content.Context;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebkitPlugin;
import org.rainboweleven.rbridge.impl.REventsCenter;

/**
 * Created by chenshaomou on 24/03/2018.
 */
public class EventsPlugin extends RWebkitPlugin {

    public static final String MODULE_NAME = "events";
    public static final String SEND_EVENT = "send";

    private Context mContext;

    public EventsPlugin(Context context) {
        mContext = context;
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
                // 统一发给事件中心处理
                REventsCenter.getInstance(mContext).sendByH5(eventName, eventParams);
                promise.setResult("true");// 事件发送成功

            } catch (Exception e) {
                e.printStackTrace();
                promise.setResult(e.getMessage());// 事件发送失败
            }
        }
    }
}
