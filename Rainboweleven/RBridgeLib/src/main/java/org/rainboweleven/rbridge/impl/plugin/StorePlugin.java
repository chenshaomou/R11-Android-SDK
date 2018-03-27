package org.rainboweleven.rbridge.impl.plugin;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

import java.util.Iterator;
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
    public static final String METHOD_GET = "get";
    public static final String METHOD_SET = "set";
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
    public void onPluginCalled(String module, String method, String params, RPromise promise) {
        if (!MODULE_NAME.equals(module)) {
            return;
        }
        try {
            // 获取数据
            if (METHOD_GET.equals(method)) {
                String key = params;
                String value = mSharedPreferences.getString(key, null);
                promise.setResult(value);
            }
            // 存储数据
            else if (METHOD_SET.equals(method)) {
                JSONObject jsonParams = new JSONObject(params);
                Iterator<String> keys = jsonParams.keys();
                boolean success = true;
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = jsonParams.getString(key);
                    if (value != null) {
                        success = success && mSharedPreferences.edit().putString(key, value).commit();
                    }
                }
                promise.setResult(success + "");
            }
            // 获取全部数据
            else if (METHOD_GET_ALL.equals(method)) {
                Map<String, ?> results = mSharedPreferences.getAll();
                String jstr = new JSONObject(results).toString();
                promise.setResult(jstr);
            }
            // 移除
            else if (METHOD_REMOVE.equals(method)) {
                String key = params;
                boolean success = mSharedPreferences.edit().remove(key).commit();
                promise.setResult(success + "");
            }
            // 移除全部
            else if (METHOD_REMOVE_ALL.equals(method)) {
                boolean success = mSharedPreferences.edit().clear().commit();
                promise.setResult(success + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            promise.setResult(e.getMessage());
        }
    }
}
