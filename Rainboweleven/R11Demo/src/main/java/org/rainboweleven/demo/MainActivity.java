package org.rainboweleven.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONObject;
import org.rainboweleven.R;
import org.rainboweleven.rbridge.JsBridge;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.impl.JsResult;
import org.rainboweleven.rbridge.impl.RSystemWebView;

public class MainActivity extends AppCompatActivity {

    private RSystemWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webView);

        // 加载html
        String path = "file:///android_asset/test.html";
        JsBridge.getInstance().loadLocalURL(mWebView, path, null);
    }

    // 按钮点击
    public void btnClick(View view) {
        // 执行JS插件
        JsBridge.getInstance().call(mWebView, "store", "getAll", new JSONObject(), new
                OnCallJsResultListener<JsResult>() {
            @Override
            public void onCallJsResult(JsResult jsResult) {
                // 执行结果
            }
        });
    }
}
