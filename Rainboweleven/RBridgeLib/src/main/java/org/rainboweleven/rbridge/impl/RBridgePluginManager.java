package org.rainboweleven.rbridge.impl;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;

import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.core.RWebkitPlugin;
import org.rainboweleven.rbridge.impl.plugin.AppInfoPlugin;
import org.rainboweleven.rbridge.impl.plugin.EventsPlugin;
import org.rainboweleven.rbridge.impl.plugin.NetworkPlugin;
import org.rainboweleven.rbridge.impl.plugin.StorePlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 插件管理器
 *
 * @author andy(Andy)
 * @datetime 2017-12-12 21:31 GMT+8
 * @email 411086563@qq.com
 */
public class RBridgePluginManager {

    // WebView
    private RWebViewInterface mWebViewInterface;

    // 所有插件
    private Map<String, RWebkitPlugin> mPluginMap = new HashMap<>();
    // WebView是否可用
    private boolean mIsRWebViewReady = false;
    // 待运行的任务
    private List<Runnable> mRunnables = new ArrayList<>();
    // 事件插件
    private EventsPlugin mEventsPlugin;

    public RBridgePluginManager(RWebViewInterface webViewInterface) {
        mWebViewInterface = webViewInterface;
    }

    // 初始化脚本
    private void initScript() {
        String webViewType = "ADCRMWV";
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            webViewType = "ADWKWV";
        }
        String webId = UUID.randomUUID().toString();
        String script = String.format(RWebViewInterface.INIT_SCRIPT, webViewType, webId);
        mWebViewInterface.evaluateJavascript(script, null);
    }

    // 初始化sdk插件
    private void initPlugins() {
        // 存储插件，传递String参数，返回String结果
        register(mWebViewInterface, StorePlugin.MODULE_NAME, StorePlugin.METHOD_SET_VALUE, new StorePlugin
                (mWebViewInterface.context()));
        register(mWebViewInterface, StorePlugin.MODULE_NAME, StorePlugin.METHOD_GET_VALUE, new StorePlugin
                (mWebViewInterface.context()));
        register(mWebViewInterface, StorePlugin.MODULE_NAME, StorePlugin.METHOD_GET_ALL, new StorePlugin
                (mWebViewInterface.context()));
        register(mWebViewInterface, StorePlugin.MODULE_NAME, StorePlugin.METHOD_REMOVE, new StorePlugin
                (mWebViewInterface.context()));
        register(mWebViewInterface, StorePlugin.MODULE_NAME, StorePlugin.METHOD_REMOVE_ALL, new StorePlugin
                (mWebViewInterface.context()));
        // 网络插件
        register(mWebViewInterface, NetworkPlugin.MODULE_NAME, NetworkPlugin.METHOD_GET, new NetworkPlugin());
        register(mWebViewInterface, NetworkPlugin.MODULE_NAME, NetworkPlugin.METHOD_POST, new NetworkPlugin());
        // 版本插件
        register(mWebViewInterface, AppInfoPlugin.MODULE_NAME, AppInfoPlugin.METHOD_VERSION, new AppInfoPlugin
                (mWebViewInterface.context()));
        // 事件插件
        mEventsPlugin = new EventsPlugin();
        register(mWebViewInterface, EventsPlugin.MODULE_NAME, EventsPlugin.SEND_EVENT, mEventsPlugin);
    }

    /**
     * WebView已经初始化好了
     */
    public void onRWebViewReady() {
        if (mIsRWebViewReady) {
            return;
        }
        initScript();
        initPlugins();
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
    }

    /**
     * WebView未初始化
     */
    public void onRWebViewNotReady() {
        // 移除注册的插件
        mPluginMap.clear(); // FIXME 只是情况插件可能存在内存泄漏，应该给插件开一个onDestroy方法
        mIsRWebViewReady = false;
    }

    // 注册插件
    public void register(final RWebViewInterface webViewInterface, final String module, final String method, final
    RWebkitPlugin plugin) {
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
                        OnCallJsResultListener() {
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

    // 运行本地异步插件
    public String runNativePlugin(final RWebViewInterface webViewInterface, final String module, final String method,
                                  final String params, final String jsCallback) {
        String key = getKey(module, method);
        final RWebkitPlugin actualTypePlugin = mPluginMap.get(key);
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
                                RBridgePluginManager.this.onCallPluginResult(webViewInterface, result, jsCallback);
                            }
                        });
                        actualTypePlugin.onPluginCalled(module, method, params, p);
                    } else {
                        // 没有找到插件
                        RBridgePluginManager.this.onCallPluginResult(webViewInterface, "插件没有找到", jsCallback);
                    }

                }
            });
            return "";
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
                String callScript = String.format(RWebViewInterface.CALL_JS_BRIDGE_CALLBACK, jsCallback, result);
                // 删除对应的JS callback
                String delScript = String.format(RWebViewInterface.DELETE_JS_BRIDGE_CALLBACK, jsCallback);
                // javascript:
                String script = String.format("javascript:%s%s", callScript, delScript);
                webViewInterface.evaluateJavascript(script, null);
            }
        };
        if (mIsRWebViewReady) {
            //可以执行优化 不要频繁调用js
            runnable.run();
        } else {
            mRunnables.add(runnable);
        }
    }

    /**
     * 监听来自JS的事件
     *
     * @param eventName
     * @param observer
     */
    public void on(String eventName, EventObserver observer) {
        if (mEventsPlugin == null) {
            return;
        }
        mEventsPlugin.addObserver(eventName, observer);
    }

    /**
     * 解除监听来自JS的事件
     *
     * @param eventName
     * @param observer
     */
    public void off(String eventName, EventObserver observer) {
        if (mEventsPlugin == null) {
            return;
        }
        mEventsPlugin.removeObserver(eventName, observer);
    }

    // 获取插件的key
    private static String getKey(String module, String method) {
        return module + "." + method;
    }
}
