package org.rainboweleven.rbridge.impl;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;

import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebViewInterface;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.core.RWebkitPlugin;
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

    // 插件
    private Map<String, RWebkitPlugin> mPluginMap = new HashMap<>();
    // 是否可用
    private boolean mIsRWebViewReady = false;
    // 待运行的任务
    private List<Runnable> mRunnables = new ArrayList<>();
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

    private void initScript(RWebViewInterface webViewInterface) {
        String webViewType = "ADCRMWV";
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
            webViewType = "ADWKWV";
        }

        String script = String.format(RWebViewInterface.INIT_SCRIPT, webViewType,webViewInterface.getWebviewId());
        webViewInterface.evaluateJavascript(script, null);
    }

    // 初始化sdk插件
    private void initPlugins(RWebViewInterface webViewInterface) {
        // 存储插件，传递String参数，返回String结果
        RWebkitPlugin storePlugin = new StorePlugin(webViewInterface.context());
        String module = StorePlugin.MODULE_NAME;
        register(webViewInterface, module, StorePlugin.METHOD_SET_VALUE, storePlugin);
        register(webViewInterface, module, StorePlugin.METHOD_GET_VALUE, storePlugin);
        register(webViewInterface, module, StorePlugin.METHOD_GET_ALL, storePlugin);
        register(webViewInterface, module, StorePlugin.METHOD_REMOVE, storePlugin);
        register(webViewInterface, module, StorePlugin.METHOD_REMOVE_ALL, storePlugin);
        // 网络插件

        // 版本插件
    }

    // WebView已经初始化好了
    public void onRWebViewReady(RWebViewInterface webViewInterface) {
        if (mIsRWebViewReady) {
            return;
        }
        initScript(webViewInterface);
        initPlugins(webViewInterface);
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

    // WebView未初始化好了
    public void onRWebViewNotReady(RWebViewInterface webViewInterface) {
        // TODO 移除注册的插件
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

    // 运行本地异步插件
    public String runNativePlugin(final RWebViewInterface webViewInterface, final String module,final String method,final String
            params,final String jsCallback) {
        String key = getKey(module, method);
        final RWebkitPlugin actualTypePlugin = mPluginMap.get(key);
        final RPromise p = new RPromise();
        //没有callback 异步
        if (jsCallback == null){
            actualTypePlugin.onPluginCalled(module, method, params,p);
            return p.getResult();
        }else{
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
                        actualTypePlugin.onPluginCalled(module, method, params,p);
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
                //javascript:
                String script =  String.format("javascript:%s%s", callScript,delScript);
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

    // 获取插件的key
    private static String getKey(String module, String method) {
        return module + "." + method;
    }
}
