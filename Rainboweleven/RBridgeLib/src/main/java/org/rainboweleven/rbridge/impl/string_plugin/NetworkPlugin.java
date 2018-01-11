package org.rainboweleven.rbridge.impl.string_plugin;

import android.util.Log;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.base_plugin.BaseStringAsyncPlugin;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class NetworkPlugin extends BaseStringAsyncPlugin {

    public static final String MODULE_NAME = "network";
    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";

    @Override
    public void onPluginCalled(String module, String method, String params, final OnCallPluginListener<String>
            listener) {
        if (!MODULE_NAME.equals(module)) {
            return;
        }
        try {
            JSONObject jsonParams = new JSONObject(params);
            // 请求url
            String url = jsonParams.optString("url");
            // 请求方式，POST和GET
            String httpMethod = jsonParams.optString("method");
            // 请求参数, 当method为post时才有参数，method为get时参数在url中
            JSONObject httpParams = jsonParams.optJSONObject("params");

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url);
            // post请求
            if (METHOD_POST.equalsIgnoreCase(httpMethod) && httpParams != null) {
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), httpParams
                        .toString());
                builder.method(httpMethod, body);
            }
            Request request = builder.build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (listener != null) {
                        // 返回异常信息
                        listener.onCallPluginResult("异常,e:" + e.getMessage());
                        listener.onCallPluginFinish();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (listener != null) {
                        // 返回数据
                        listener.onCallPluginResult(response.body().string());
                        listener.onCallPluginFinish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                // 返回异常信息
                listener.onCallPluginResult("异常,e:" + e.getMessage());
                listener.onCallPluginFinish();
            }
        }
    }

    // Demo测试用
    private void forTest(String module, String method, String params, final OnCallPluginListener<String> listener) {
        String result = "NetworkPlugin(StringPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("andy", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listener != null) {
            listener.onCallPluginResult(result);
            listener.onCallPluginFinish();
        }
    }
}
