package org.rainboweleven.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.rainboweleven.R;
import org.rainboweleven.rbridge.JsBridge;
import org.rainboweleven.rbridge.core.RBridgeAsyncPlugin;
import org.rainboweleven.rbridge.core.RWebViewInterface.OnCallJsResultListener;
import org.rainboweleven.rbridge.impl.RSystemWebView;
import org.rainboweleven.rbridge.impl.string_plugin.AppInfoPlugin;
import org.rainboweleven.rbridge.impl.string_plugin.NetworkPlugin;

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
        JsBridge.getInstance().register(mWebView, AppInfoPlugin.MODULE_NAME, AppInfoPlugin.METHOD_VERSION,
                appInfoPlugin);

        // 注册网络插件
        NetworkPlugin networkPlugin = new NetworkPlugin();
        JsBridge.getInstance().register(mWebView, NetworkPlugin.MODULE_NAME, NetworkPlugin.METHOD_GET, networkPlugin);
        JsBridge.getInstance().register(mWebView, NetworkPlugin.MODULE_NAME, NetworkPlugin.METHOD_POST, networkPlugin);
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
        JsBridge.getInstance().call(mWebView, null, "testNavCall", params, new OnCallJsResultListener<String>() {
            @Override
            public void onCallJsResult(String result) {
                // 执行结果
                Log.e("wlf", "执行结果：result：" + result);
            }
        });
    }
}
