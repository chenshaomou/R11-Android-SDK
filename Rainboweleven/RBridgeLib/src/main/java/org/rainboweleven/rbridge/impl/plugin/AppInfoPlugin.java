package org.rainboweleven.rbridge.impl.plugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

/**
 * APP信息插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2018-01-04 09:42 GMT+8
 * @email 411086563@qq.com
 */
public class AppInfoPlugin extends RWebkitPlugin {

    public static final String MODULE_NAME = "appInfo";
    public static final String METHOD_VERSION = "version";

    private Context mContext;

    public AppInfoPlugin(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onPluginCalled(String module, String method, String params,RPromise promise) {

        if (!MODULE_NAME.equals(module)) {
            return;
        }
        // 获取版本号
        if (METHOD_VERSION.equals(method)) {
            promise.setResult(getAppVersionName(mContext));
        }
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
