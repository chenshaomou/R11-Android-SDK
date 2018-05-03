package org.rainboweleven.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.rainboweleven.R;
import org.rainboweleven.rbridge.JsBridge;
import org.rainboweleven.rbridge.core.RWebViewInterface.EventObserver;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.impl.RSystemWebView;

public class MainActivity extends AppCompatActivity {

    private RSystemWebView mWebView;

    private EventObserver mObserver = new EventObserver() {
        @Override
        public void onObserver(String eventName, String params) {
            Log.e("wlf","原生收到pageFinish事件(可能来自H5)");
            Toast.makeText(MainActivity.this,"原生收到pageFinish事件(可能来自H5)",Toast.LENGTH_SHORT).show();
        }
    };

    private EventObserver mObserver2 = new EventObserver() {
        @Override
        public void onObserver(String eventName, String params) {
            Log.e("wlf","原生收到onPayFinish事件(可能来自原生)");
            Toast.makeText(MainActivity.this,"原生收到onPayFinish事件(可能来自原生)",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webView);

        // 加载html
        String path = "file:///android_asset/html5/index.html";
        JsBridge.getInstance().loadLocalURL(mWebView, path, null);

        // 注册获取APP版本信息插件
        // AppInfoPlugin appInfoPlugin = new AppInfoPlugin(this);
        // JsBridge.getInstance().register(mWebView, AppInfoPlugin.MODULE_NAME, AppInfoPlugin.METHOD_VERSION, appInfoPlugin);

        // 注册监听pageFinish事件
        JsBridge.getInstance().on(this, "pageFinish", mObserver);

        // 注册监听onPayFinish事件
        JsBridge.getInstance().on(this, "onPayFinish", mObserver2);
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

    // 按钮点击
    public void btnEventClick(View view) {
        // 发送原生事件（H5和原生都要收得到）
        JsBridge.getInstance().send(this,"onPayFinish", "{\"orderNO\":\"11931398\"}");
    }

    // 按钮点击
    public void btnEventOffClick(View view) {
        // 取消注册
        JsBridge.getInstance().off(MainActivity.this, "onPayFinish", mObserver2);
    }

    // 按钮点击
    public void btnEventH5OffClick(View view) {
        // 取消注册
        JsBridge.getInstance().off(MainActivity.this, "pageFinish", mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JsBridge.getInstance().release(this);
    }
}
