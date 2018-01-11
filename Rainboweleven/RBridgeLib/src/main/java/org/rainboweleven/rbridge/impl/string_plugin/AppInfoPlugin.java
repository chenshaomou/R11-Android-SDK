package org.rainboweleven.rbridge.impl.string_plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.rainboweleven.rbridge.impl.base_plugin.BaseStringPlugin;
import org.rainboweleven.rbridge.util.CreateHtmlPluginUtil;

/**
 * APP信息插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2018-01-04 09:42 GMT+8
 * @email 411086563@qq.com
 */
public class AppInfoPlugin extends BaseStringPlugin {

    public static final String MODULE_NAME = "appInfo";
    public static final String METHOD_VERSION = "version";

    private Context mContext;

    public AppInfoPlugin(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public String onPluginCalled(String module, String method, String params) {
        if (!MODULE_NAME.equals(module)) {
            return null;
        }
        // 获取版本号
        if (METHOD_VERSION.equals(method)) {
            return getAppVersionName(mContext);
        }
        return null;
    }

    @Override
    public String onGetCreatePluginScript(String module, String method) {
        if (!MODULE_NAME.equals(module)) {
            return super.onGetCreatePluginScript(module, method);
        }
        // 获取版本号
        if (METHOD_VERSION.equals(method)) {
            // 生成window.jsBridge.$module.$method()
            String function = "function (){return window.jsBridge.call('" + module + "','" + method + "',{})}";
            return CreateHtmlPluginUtil.getCreatePluginScript(module, method, function);
        }
        return super.onGetCreatePluginScript(module, method);
    }

    // 获取APP版本号
    private static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
