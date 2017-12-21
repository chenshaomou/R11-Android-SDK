package org.rainboweleven.rbridge.core;

import android.text.TextUtils;

import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin.OnCallPluginListener;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件管理器
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:31 GMT+8
 * @email 411086563@qq.com
 */
public class RBridgePluginManager<PARAMS, RESULT> {

    // 同步插件
    private Map<String, RBridgePlugin<PARAMS, RESULT>> mPluginMap = new HashMap<>();
    // 异步插件
    private Map<String, RBridgeAsyncPlugin<PARAMS, RESULT>> mPluginAsyncMap = new HashMap<>();

    // 注册插件
    public void register(RWebViewInterface webViewInterface, String module, String method, RBridgePlugin<PARAMS,
            RESULT> plugin) {
        if (webViewInterface == null) {
            return;
        }
        // 生成 window.jsBridge.module.method(params, callback)
        webViewInterface.evaluateJavascript(getCreatePluginScript(module, method), null);
        // 插件存储
        String key = getKey(module, method);
        mPluginMap.put(key, plugin);
    }

    // 注册异步插件
    public void register(RWebViewInterface webViewInterface, String module, String method, RBridgeAsyncPlugin<PARAMS,
            RESULT> plugin) {
        if (webViewInterface == null) {
            return;
        }
        // 生成 window.jsBridge.module.method(params, callback)
        webViewInterface.evaluateJavascript(getCreatePluginScript(module, method), null);
        // 插件存储
        String key = getKey(module, method);
        mPluginAsyncMap.put(key, plugin);
    }

    // 运行本地插件
    public RESULT runNativePlugin(String module, String method, PARAMS params) {
        String key = getKey(module, method);
        return mPluginMap.get(key).onPluginCalled(module, method, params);
    }

    // 运行本地异步插件
    public void runNativePlugin(String module, String method, PARAMS params, OnCallPluginListener<RESULT> listener) {
        String key = getKey(module, method);
        mPluginAsyncMap.get(key).onPluginCalled(module, method, params, listener);
    }

    // 获取插件的key
    private static String getKey(String module, String method) {
        return module + "." + method;
    }

    // 将插件生成到 window.jsBridge.module.method(params, callback)
    private static String getCreatePluginScript(String module, String method) {
        if (TextUtils.isEmpty(module)) {
            module = RWebViewInterface.MODULE_DEFAULT;
        }
        String script = String.format(RWebViewInterface.CREATE_PLUGIN_IN_JS_BRIDGE, module, module, module, method,
                module, method, module, method, module, method);
        return script;
    }
}
