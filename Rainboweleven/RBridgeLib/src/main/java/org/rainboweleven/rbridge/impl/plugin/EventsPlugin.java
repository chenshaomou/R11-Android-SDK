package org.rainboweleven.rbridge.impl.plugin;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

/**
 * Created by chenshaomou on 24/03/2018.
 */

public class EventsPlugin extends RWebkitPlugin {

    public static final String MODULE_NAME = "events";
    public static final String SEND_EVENT = "send";

    private Context mContext;

    public EventsPlugin(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onPluginCalled(String module, String method, String params, RPromise promise) {

        if (!MODULE_NAME.equals(module)) {
            return;
        }
        // 获取版本号
        if (SEND_EVENT.equals(method)) {

            try{

                JSONObject jsonParams = new JSONObject(params);
                String action = jsonParams.optString("eventName");
                String _params = jsonParams.optString("params");

                Intent i = new Intent();
                i.setAction(action);
                i.putExtra("params",_params);
                mContext.sendBroadcast(i);
                promise.setResult("");

            }catch (Exception e){
                promise.setResult("");
            }

        }
    }
}
