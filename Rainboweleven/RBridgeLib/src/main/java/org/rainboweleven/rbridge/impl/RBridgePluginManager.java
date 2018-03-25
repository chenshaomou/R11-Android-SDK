package org.rainboweleven.rbridge.impl;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;

import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.core.RWebkitPlugin;
import org.rainboweleven.rbridge.impl.plugin.EventsPlugin;
import org.rainboweleven.rbridge.impl.plugin.NetworkPlugin;
import org.rainboweleven.rbridge.impl.plugin.StorePlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 插件管理器，支持管理多个WebView实例以及它们的插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:31 GMT+8
 * @email 411086563@qq.com
 */
public class RBridgePluginManager {

    /**
     * WebView接口
     */
    private Map<RWebViewInterface, WebViewPluginManager> mWebViewPluginsMap = new HashMap<>();
    // 单例
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

    /**
     * WebView已经初始化好了
     *
     * @param rWebViewInterface
     */
    public void onRWebViewReady(RWebViewInterface rWebViewInterface) {
        WebViewPluginManager pluginManager = mWebViewPluginsMap.get(rWebViewInterface);
        if (pluginManager == null) {
            pluginManager = new WebViewPluginManager(rWebViewInterface);
            mWebViewPluginsMap.put(rWebViewInterface, pluginManager);
        }
        pluginManager.onRWebViewReady();
    }

    /**
     * WebView未初始化
     *
     * @param rWebViewInterface
     */
    public void onRWebViewNotReady(RWebViewInterface rWebViewInterface) {
        WebViewPluginManager pluginManager = mWebViewPluginsMap.get(rWebViewInterface);
        if (pluginManager != null) {
            pluginManager.onRWebViewNotReady();
        }
    }

    /**
     * 注册插件
     *
     * @param rWebViewInterface
     * @param module
     * @param method
     * @param plugin
     */
    public void register(RWebViewInterface rWebViewInterface, String module, String method, RWebkitPlugin plugin) {
        WebViewPluginManager pluginManager = mWebViewPluginsMap.get(rWebViewInterface);
        if (pluginManager == null) {
            pluginManager = new WebViewPluginManager(rWebViewInterface);
            mWebViewPluginsMap.put(rWebViewInterface, pluginManager);
        }
        pluginManager.register(module, method, plugin);
    }

    /**
     * 执行插件
     *
     * @param rWebViewInterface
     * @param module
     * @param method
     * @param params
     * @param jsCallback
     * @return
     */
    public String runNativePlugin(final RWebViewInterface rWebViewInterface, final String module, final String
            method, final String params, final String jsCallback) {
        WebViewPluginManager pluginManager = mWebViewPluginsMap.get(rWebViewInterface);
        if (pluginManager != null) {
            return pluginManager.runNativePlugin(module, method, params, jsCallback);
        }
        return null;
    }

    // 管理一个WebView的插件的生命周期以及执行
    private static class WebViewPluginManager {
        // WebView接口
        private RWebViewInterface mRWebViewInterface;
        // 所有插件
        private Map<String, RWebkitPlugin> mPluginsMap = new HashMap<>();
        // 当前WebView是否可用
        private boolean mIsRWebViewReady = false;
        // 待运行的任务
        private List<Runnable> mRunnables = new ArrayList<>();

        private WebViewPluginManager(RWebViewInterface RWebViewInterface) {
            mRWebViewInterface = RWebViewInterface;
        }

        // 初始化脚本
        private void initScript() {
            String webViewType = "ADCRMWV";
            if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
                webViewType = "ADWKWV";
            }
            String webId = UUID.randomUUID().toString();// WebView Id 暂时随机生成
            String script = String.format(RWebViewInterface.INIT_SCRIPT, webViewType, webId);
            mRWebViewInterface.evaluateJavascript(script, null);
        }

        // 初始化sdk插件
        private void initPlugins() {
            // 存储插件
            register(StorePlugin.MODULE_NAME, StorePlugin.METHOD_SET_VALUE, new StorePlugin(mRWebViewInterface
                    .context()));
            register(StorePlugin.MODULE_NAME, StorePlugin.METHOD_GET_VALUE, new StorePlugin(mRWebViewInterface
                    .context()));
            register(StorePlugin.MODULE_NAME, StorePlugin.METHOD_GET_ALL, new StorePlugin(mRWebViewInterface.context
                    ()));
            register(StorePlugin.MODULE_NAME, StorePlugin.METHOD_REMOVE, new StorePlugin(mRWebViewInterface.context()));
            register(StorePlugin.MODULE_NAME, StorePlugin.METHOD_REMOVE_ALL, new StorePlugin(mRWebViewInterface
                    .context()));
            // 网络插件
            register(NetworkPlugin.MODULE_NAME, NetworkPlugin.METHOD_GET, new NetworkPlugin());
            register(NetworkPlugin.MODULE_NAME, NetworkPlugin.METHOD_POST, new NetworkPlugin());
            // 版本插件
            // register(AppInfoPlugin.MODULE_NAME, AppInfoPlugin.METHOD_VERSION, new AppInfoPlugin(mRWebViewInterface
            // .context()));
            // 事件插件
            EventsPlugin eventsPlugin = new EventsPlugin();
            register(EventsPlugin.MODULE_NAME, EventsPlugin.SEND_EVENT, eventsPlugin);
        }

        /**
         * WebView已经初始化好了
         */
        private void onRWebViewReady() {
            if (mIsRWebViewReady) {
                return;
            }
            initScript();
            initPlugins();
            // WebView is Ready
            mIsRWebViewReady = true;
            // 执行待运行的任务
            if (!mRunnables.isEmpty()) {
                for (Runnable runnable : mRunnables) {
                    if (runnable == null) {
                        continue;
                    }
                    runnable.run();
                }
            }
            mRunnables.clear();
        }

        /**
         * WebView未初始化
         */
        private void onRWebViewNotReady() {
            // 移除注册的插件
            mPluginsMap.clear(); // FIXME 直接移除插件可能存在内存泄漏，应该给插件开一个onDestroy方法
            // 移除待运行的任务
            mRunnables.clear();
            // WebView is Not Ready
            mIsRWebViewReady = false;
        }

        // 注册插件
        private void register(final String module, final String method, final RWebkitPlugin plugin) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!mIsRWebViewReady) {
                        return;
                    }
                    // 生成 window.jsBridge.module.method(params, callback)
                    mRWebViewInterface.evaluateJavascript(plugin.onGetCreatePluginScript(module, method), new
                            OnCallJsResultListener() {
                        @Override
                        public void onCallJsResult(String result) {
                            // 插件存储
                            String key = getKey(module, method);
                            mPluginsMap.put(key, plugin);
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

        // 运行本地异步插件
        private String runNativePlugin(final String module, final String method, final String params, final String
                jsCallback) {
            String key = getKey(module, method);
            final RWebkitPlugin actualTypePlugin = mPluginsMap.get(key);
            final RPromise p = new RPromise();
            //没有callback 异步
            if (jsCallback == null) {
                actualTypePlugin.onPluginCalled(module, method, params, p);
                return p.getResult();
            } else {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (actualTypePlugin != null) {
                            p.setListener(new RPromise.OnCallPluginListener() {
                                @Override
                                public void onCallPluginResult(String result) {
                                    WebViewPluginManager.this.onCallPluginResult(result, jsCallback);
                                }
                            });
                            actualTypePlugin.onPluginCalled(module, method, params, p);
                        } else {
                            // 没有找到插件
                            WebViewPluginManager.this.onCallPluginResult("插件没有找到", jsCallback);
                        }

                    }
                });
                return "";
            }
        }

        // 执行结果回调
        private void onCallPluginResult(final String result, final String jsCallback) {
            if (mRWebViewInterface == null) {
                return;
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!mIsRWebViewReady) {
                        return;
                    }
                    // 异步JS回调
                    String callScript = String.format(RWebViewInterface.CALL_JS_BRIDGE_CALLBACK, jsCallback, result);
                    // 删除对应的JS callback
                    String delScript = String.format(RWebViewInterface.DELETE_JS_BRIDGE_CALLBACK, jsCallback);
                    // javascript:
                    String script = String.format("javascript:%s%s", callScript, delScript);
                    mRWebViewInterface.evaluateJavascript(script, null);
                }
            };
            if (mIsRWebViewReady) {
                //可以执行优化 不要频繁调用js
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
}
