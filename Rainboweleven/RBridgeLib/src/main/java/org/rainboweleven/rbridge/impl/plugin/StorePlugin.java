package org.rainboweleven.rbridge.impl.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

import java.util.Map;

/**
 * 存储插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-17 21:23 GMT+8
 * @email 411086563@qq.com
 */
public class StorePlugin extends RWebkitPlugin {

    public static final String MODULE_NAME = "store";
    public static final String METHOD_GET_VALUE = "getValue";
    public static final String METHOD_SET_VALUE = "setValue";
    public static final String METHOD_GET_ALL = "getAll";
    public static final String METHOD_REMOVE = "remove";
    public static final String METHOD_REMOVE_ALL = "removeAll";

    // 本地store名字
    private static final String STORE_PLUGIN_NAME = "rBridge.StorePlugin";

    private SharedPreferences mSharedPreferences;

    public StorePlugin(Context context) {
        mSharedPreferences = context.getSharedPreferences(STORE_PLUGIN_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String onPluginCalled(String module, String method, String params,OnCallPluginListener listener) {
        if (!MODULE_NAME.equals(module)) {
            return null;
        }
        try {
            JSONObject jsonParams = new JSONObject(params);
            // 获取数据
            if (METHOD_GET_VALUE.equals(method)) {
                String key = jsonParams.optString("key");
                String value = mSharedPreferences.getString(key, null);
                return value;
            }
            // 存储数据
            else if (METHOD_SET_VALUE.equals(method)) {
                String key = jsonParams.optString("key");
                String value = jsonParams.optString("value");
                boolean success = mSharedPreferences.edit().putString(key, value).commit();
                return success + "";
            }
            // 获取全部数据
            else if (METHOD_GET_ALL.equals(method)) {
                Map<String, ?> results = mSharedPreferences.getAll();
                return new JSONObject(results).toString();
            }
            // 移除
            else if (METHOD_REMOVE.equals(method)) {
                String key = jsonParams.optString("key");
                boolean success = mSharedPreferences.edit().remove(key).commit();
                return success + "";
            }
            // 移除全部
            else if (METHOD_REMOVE_ALL.equals(method)) {
                boolean success = mSharedPreferences.edit().clear().commit();
                return success + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
