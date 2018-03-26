package org.rainboweleven.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.rainboweleven.R;
import org.rainboweleven.rbridge.JsBridge;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.impl.RSystemWebView;
import org.rainboweleven.rbridge.impl.plugin.AppInfoPlugin;

public class MainActivity extends AppCompatActivity {

    private RSystemWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webView);

        // 加载html
        String path = "file:///android_asset/html5/index.html";
        JsBridge.getInstance().loadLocalURL(mWebView, path, null);

        // 注册获取APP版本信息插件
        AppInfoPlugin appInfoPlugin = new AppInfoPlugin(this);
        JsBridge.getInstance().register(mWebView, AppInfoPlugin.MODULE_NAME, AppInfoPlugin.METHOD_VERSION, appInfoPlugin);

        EventObserver observer = new EventObserver() {
            @Override
            public void onObserver(String eventName, String params) {
                // H5加载完成，隐藏loading框
                Log.e("wlf","收到pageFinish事件(可能来自H5)");
                // 取消注册
                JsBridge.getInstance().off(MainActivity.this, "pageFinish", this);
            }
        };

        // 注册监听pageFinish事件
        JsBridge.getInstance().on(this, "pageFinish", observer);

        EventObserver observer2 = new EventObserver() {
            @Override
            public void onObserver(String eventName, String params) {
                Log.e("wlf","收到onPayFinish事件(可能来自原生)");
                // 取消注册
                JsBridge.getInstance().off(MainActivity.this, "onPayFinish", this);
            }
        };
        // 注册监听onPayFinish事件
        JsBridge.getInstance().on(this, "onPayFinish", observer2);

        // 发送原生事件（H5和原生都要收得到）
        JsBridge.getInstance().send(this,"onPayFinish", "{'orderNO':'11931398'}");
    }

    // 按钮点击
    public void btnClick(View view) {
        // 执行JS插件
        JSONObject params = new JSONObject();
        try {
            params.put("key", "abc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsBridge.getInstance().call(mWebView, "testNavCall", params, new OnCallJsResultListener() {
            @Override
            public void onCallJsResult(String result) {
                // 执行结果
                Log.e("wlf", "执行结果：result：" + result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JsBridge.getInstance().release(this);
    }
}
