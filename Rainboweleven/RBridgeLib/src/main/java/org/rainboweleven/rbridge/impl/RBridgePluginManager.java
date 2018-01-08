package org.rainboweleven.rbridge.impl;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin.OnCallPluginListener;
import org.rainboweleven.rbridge.core.RBridgePlugin;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.impl.object_plugin.EventPlugin;
import org.rainboweleven.rbridge.util.GenericUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件管理器
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:31 GMT+8
 * @email 411086563@qq.com
 */
public class RBridgePluginManager {

    // 同步插件
    private Map<String, RBridgePlugin<?, ?>> mPluginMap = new HashMap<>();
    // 异步插件
    private Map<String, RBridgeAsyncPlugin<?, ?>> mPluginAsyncMap = new HashMap<>();

    // 是否可用
    private boolean mIsRWebViewReady = false;
    // 待运行的任务
    private List<Runnable> mRunnables = new ArrayList<>();

    private static RBridgePluginManager sInstance;

    /**
     * 获取单例
     *
     * @return
     */
    public static RBridgePluginManager getInstance() {
        if (sInstance == null) {
            synchronized (RBridgePluginManager.class) {
                if (sInstance == null) {
                    sInstance = new RBridgePluginManager();
                }
            }
        }
        return sInstance;
    }

    // 构造方法
    private RBridgePluginManager() {
    }

    private void initScript(RWebViewInterface webViewInterface) {
        String script = String.format(RWebViewInterface.INIT_SCRIPT, "ADCRMWV");
        webViewInterface.evaluateJavascript(script, null);
    }

    // 初始化sdk插件
    private void initPlugins(RWebViewInterface webViewInterface) {
        // 存储插件，传递JSONObject参数，返回PluginResult结果
        RBridgePlugin objectStorePlugin = new org.rainboweleven.rbridge.impl.object_plugin.StorePlugin();
        register(webViewInterface, "store", "setValue", objectStorePlugin);
        register(webViewInterface, "store", "getValue", objectStorePlugin);
        register(webViewInterface, "store", "getAll", objectStorePlugin);
        register(webViewInterface, "store", "remove", objectStorePlugin);
        register(webViewInterface, "store", "removeAll", objectStorePlugin);

        // 存储插件，传递String参数，返回String结果
        RBridgePlugin stringStorePlugin = new org.rainboweleven.rbridge.impl.string_plugin.StorePlugin(webViewInterface.context());
        register(webViewInterface, "store", "setValue", stringStorePlugin);
        register(webViewInterface, "store", "getValue", stringStorePlugin);
        register(webViewInterface, "store", "getAll", stringStorePlugin);
        register(webViewInterface, "store", "remove", stringStorePlugin);
        register(webViewInterface, "store", "removeAll", stringStorePlugin);
    }

    // 初始化sdk事件
    private void initEvents(RWebViewInterface webViewInterface) {
        // 事件插件
        EventPlugin eventPlugin = new EventPlugin();
        register(webViewInterface, "event", "on", eventPlugin);
        register(webViewInterface, "event", "off", eventPlugin);
        register(webViewInterface, "event", "send", eventPlugin);
    }

    // WebView已经初始化好了
    public void onRWebViewReady(RWebViewInterface webViewInterface) {
        if (mIsRWebViewReady) {
            return;
        }
        initScript(webViewInterface);
        initPlugins(webViewInterface);
        initEvents(webViewInterface);
        mIsRWebViewReady = true;
        if (!mRunnables.isEmpty()) {
            for (Runnable runnable : mRunnables) {
                if (runnable == null) {
                    continue;
                }
                runnable.run();
            }
        }
        mRunnables.clear();
        // 发送sdk ready事件
        String script = String.format(RWebViewInterface.CALL_SEND_DOCUMENT_EVENT, "deviceready");
        webViewInterface.evaluateJavascript(script, null);
    }

    // WebView未初始化好了
    public void onRWebViewNotReady(RWebViewInterface webViewInterface) {
        // TODO 移除注册的插件
        mIsRWebViewReady = false;
    }

    // 注册插件
    public void register(final RWebViewInterface webViewInterface, final String module, final String method, final
    RBridgePlugin<?, ?> plugin) {
        if (webViewInterface == null) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsRWebViewReady) {
                    return;
                }
                // 生成 window.jsBridge.module.method(params, callback)
                webViewInterface.evaluateJavascript(plugin.onGetCreatePluginScript(module, method), new
                        OnCallJsResultListener<String>() {
                    @Override
                    public void onCallJsResult(String result) {
                        // 插件存储
                        String key = getKey(module, method);
                        mPluginMap.put(key, plugin);
                    }
                });
            }
        };
        if (mIsRWebViewReady) {
            runnable.run();
        } else {
            mRunnables.add(runnable);
        }
    }

    // 注册异步插件
    public void register(final RWebViewInterface webViewInterface, final String module, final String method, final
    RBridgeAsyncPlugin<?, ?> plugin) {
        if (webViewInterface == null) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsRWebViewReady) {
                    return;
                }
                // 生成 window.jsBridge.module.method(params, callback)
                webViewInterface.evaluateJavascript(plugin.onGetCreatePluginScript(module, method), new
                        OnCallJsResultListener<String>() {
                    @Override
                    public void onCallJsResult(String result) {
                        // 插件存储
                        String key = getKey(module, method);
                        mPluginAsyncMap.put(key, plugin);
                    }
                });
            }
        };
        if (mIsRWebViewReady) {
            runnable.run();
        } else {
            mRunnables.add(runnable);
        }
    }

    // 运行本地插件
    public String runNativePlugin(String module, String method, String params) {
        String key = getKey(module, method);
        RBridgePlugin<?, ?> plugin = mPluginMap.get(key);
        try {
            Type paramsType = GenericUtil.getGenericParameterType(plugin, 0);
            Type resultType = GenericUtil.getGenericParameterType(plugin, 1);
            if (paramsType != null && resultType != null && paramsType instanceof Class && resultType instanceof
                    Class) {
                Class paramsClass = (Class) paramsType;
                Class resultClass = (Class) resultType;
                // 传递的参数为JSONObject类型，返回的结果为PluginResult类型
                if (paramsClass == JSONObject.class && resultClass == PluginResult.class) {
                    RBridgePlugin<JSONObject, PluginResult> actualTypePlugin = (RBridgePlugin<JSONObject,
                            PluginResult>) plugin;
                    JSONObject actualTypeParams = new JSONObject(params);
                    PluginResult pluginResult = actualTypePlugin.onPluginCalled(module, method, actualTypeParams);
                    return pluginResult.toJsonString();
                }
            }
        } catch (Exception e) {
            return "";
        }
        // 默认以字符串类型处理
        RBridgePlugin<String, String> actualTypePlugin = (RBridgePlugin<String, String>) plugin;
        return actualTypePlugin.onPluginCalled(module, method, params);
    }

    // 运行本地异步插件
    public void runNativePlugin(final RWebViewInterface webViewInterface, String module, String method, String
            params, final String jsCallback) {
        String key = getKey(module, method);
        RBridgeAsyncPlugin<?, ?> plugin = mPluginAsyncMap.get(key);
        boolean handled = false;
        try {
            Type paramsType = GenericUtil.getGenericParameterType(plugin, 0);
            Type resultType = GenericUtil.getGenericParameterType(plugin, 1);
            if (paramsType != null && resultType != null && paramsType instanceof Class && resultType instanceof
                    Class) {
                Class paramsClass = (Class) paramsType;
                Class resultClass = (Class) resultType;
                // 传递的参数为JSONObject类型，返回的结果为PluginResult类型
                if (paramsClass == JSONObject.class && resultClass == PluginResult.class) {
                    RBridgeAsyncPlugin<JSONObject, PluginResult> actualTypePlugin = (RBridgeAsyncPlugin<JSONObject,
                            PluginResult>) plugin;
                    JSONObject actualTypeParams = new JSONObject(params);
                    OnCallPluginListener<PluginResult> actualTypeListener = new OnCallPluginListener<PluginResult>() {
                        @Override
                        public void onCallPluginFinish() {
                            RBridgePluginManager.this.onCallPluginFinish(webViewInterface, jsCallback);
                        }

                        @Override
                        public void onCallPluginResult(PluginResult pluginResult) {
                            String result = "";
                            if (pluginResult != null) {
                                result = pluginResult.toJsonString();
                            }
                            RBridgePluginManager.this.onCallPluginResult(webViewInterface, result, jsCallback);
                        }
                    };
                    actualTypePlugin.onPluginCalled(module, method, actualTypeParams, actualTypeListener);
                    handled = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            RBridgePluginManager.this.onCallPluginResult(webViewInterface, "", jsCallback);
            RBridgePluginManager.this.onCallPluginFinish(webViewInterface, jsCallback);
            handled = true;
        }
        // 默认以字符串类型处理
        if (!handled) {
            RBridgeAsyncPlugin<String, String> actualTypePlugin = (RBridgeAsyncPlugin<String, String>) plugin;
            OnCallPluginListener<String> listener = new OnCallPluginListener<String>() {
                @Override
                public void onCallPluginFinish() {
                    RBridgePluginManager.this.onCallPluginFinish(webViewInterface, jsCallback);
                }

                @Override
                public void onCallPluginResult(String result) {
                    RBridgePluginManager.this.onCallPluginResult(webViewInterface, result, jsCallback);
                }
            };
            if (actualTypePlugin != null) {
                actualTypePlugin.onPluginCalled(module, method, params, listener);
            }
            // 没有找到插件
            else {
                RBridgePluginManager.this.onCallPluginResult(webViewInterface, "插件没有找到", jsCallback);
                RBridgePluginManager.this.onCallPluginFinish(webViewInterface, jsCallback);
            }
        }
    }

    // 完成回调
    private void onCallPluginFinish(final RWebViewInterface webViewInterface, final String jsCallback) {
        if (webViewInterface == null) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsRWebViewReady) {
                    return;
                }
                // 删除对应的JS callback
                String script = String.format(RWebViewInterface.DELETE_JS_BRIDGE_CALLBACK, jsCallback);
                webViewInterface.evaluateJavascript(script, null);
            }
        };
        if (mIsRWebViewReady) {
            runnable.run();
        } else {
            mRunnables.add(runnable);
        }
    }

    // 执行结果回调
    private void onCallPluginResult(final RWebViewInterface webViewInterface, final String result, final String
            jsCallback) {
        if (webViewInterface == null) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsRWebViewReady) {
                    return;
                }
                // 异步JS回调
                String script = String.format(RWebViewInterface.CALL_JS_BRIDGE_CALLBACK, jsCallback, result);
                webViewInterface.evaluateJavascript(script, null);
            }
        };
        if (mIsRWebViewReady) {
            runnable.run();
        } else {
            mRunnables.add(runnable);
        }
    }

    // 获取插件的key
    private static String getKey(String module, String method) {
        return module + "." + method;
    }
}
