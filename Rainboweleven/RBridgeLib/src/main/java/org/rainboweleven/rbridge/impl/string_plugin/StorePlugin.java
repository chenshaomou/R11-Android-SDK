package org.rainboweleven.rbridge.impl.string_plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.rainboweleven.rbridge.impl.base_plugin.BaseStringPlugin;

/**
 * 存储插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-17 21:23 GMT+8
 * @email 411086563@qq.com
 */
public class StorePlugin extends BaseStringPlugin {

    public static final String MODULE_NAME = "store";
    public static final String METHOD_GETVALUE = "getValue";
    public static final String METHOD_SETVALUE = "setValue";
    public static final String METHOD_GETALL = "getAll";

    private static final String STORE_PLUGIN_NAME = "rBridge.StorePlugin";

    private SharedPreferences mSharedPreferences;

    public StorePlugin(Context context) {
        mSharedPreferences = context.getSharedPreferences(STORE_PLUGIN_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String onPluginCalled(String module, String method, String params) {
        // Demo测试
        String result = "StorePlugin(StringPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("wlf", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

        /**
        // 具体实现
        if(!MODULE_NAME.equals(module)){
            return null;
        }

        try {
            JSONObject jsonParams = new JSONObject(params);

            // getValue
            if (METHOD_GETVALUE.equals(module)) {
                String key = jsonParams.optString("key");
                String value = mSharedPreferences.getString(key, null);
                return value;
            }
            // setValue
            else if (METHOD_SETVALUE.equals(module)) {
                String key = jsonParams.optString("key");
                String value = jsonParams.optString("value");
                boolean success = mSharedPreferences.edit().putString(key, value).commit();
                return success + "";
            }
            // getAll
            else if (METHOD_GETALL.equals(module)) {
                Map<String,?> results = mSharedPreferences.getAll();
                return new JSONObject(results).toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
         */
    }
}
