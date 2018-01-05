package org.rainboweleven.rbridge.util;

import android.text.TextUtils;

import org.rainboweleven.rbridge.core.RWebViewInterface;

/**
 * @author andy(Andy)
 * @datetime 2018-01-05 15:30 GMT+8
 * @email 411086563@qq.com
 */
public class CreateHtmlPluginUtil {

    // 将插件生成到 window.jsBridge.module.method(params, callback)
    /**
     * 获取生成html映射插件的脚本
     *
     * @param module
     * @param method
     * @param function
     * @return
     */
    public static String getCreatePluginScript(String module, String method, String function) {
        if (TextUtils.isEmpty(module)) {
            module = RWebViewInterface.MODULE_DEFAULT;
        }
        String script;
        if(!TextUtils.isEmpty(function)){
            // 自定义js function
            // String function = "function (key,value){var params={\"key\":key,\"value\":value};return window.jsBridge
            // .call(module,method,params)}";
            // script = String.format(RWebViewInterface.CREATE_PLUGIN_IN_JS_BRIDGE_WITH_CUSTOM_FUN, module, method, function);
             script = String.format(RWebViewInterface.CREATE_PLUGIN_IN_JS_BRIDGE_WITH_CUSTOM_FUN, module, method, function);
        }else{
            // 不自定义function
            script = String.format(RWebViewInterface.CREATE_PLUGIN_IN_JS_BRIDGE, module, method);
        }
        return script;
    }
}
