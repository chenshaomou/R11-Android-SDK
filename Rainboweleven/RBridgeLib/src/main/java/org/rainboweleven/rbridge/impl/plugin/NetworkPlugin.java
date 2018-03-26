package org.rainboweleven.rbridge.impl.plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RPromise;
import org.rainboweleven.rbridge.core.RWebkitPlugin;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络插件
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class NetworkPlugin extends RWebkitPlugin {

    public static final String MODULE_NAME = "network";
    public static final String METHOD_REQUEST = "request";
    public static final String REQUEST_METHOD_GET = "get";
    public static final String REQUEST_METHOD_POST = "post";

    @Override
    public void onPluginCalled(String module, String method, String params, final RPromise promise) {
        if (!MODULE_NAME.equals(module)) {
            return;
        }
        if(!METHOD_REQUEST.equals(method)){
            return;
        }
        try {
            JSONObject jsonParams = new JSONObject(params);
            // 请求url
            String url = jsonParams.optString("url");
            // 请求方式，POST和GET
            String httpMethod = jsonParams.optString("method");
            JSONObject httpHeader = jsonParams.optJSONObject("header");
            // 请求参数, 当method为post时才有参数，method为get时参数在url中
            JSONObject httpContent = jsonParams.optJSONObject("data");

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url);
            // post请求
            if (REQUEST_METHOD_POST.equalsIgnoreCase(httpMethod)) {
                String content = "";
                if(httpContent != null){
                    content = httpContent.toString();
                }
                String contentType = httpHeader.getString("Content-Type");
                RequestBody body = RequestBody.create(MediaType.parse(contentType), content);
                Iterator<String> keys = httpHeader.keys();
                while (keys.hasNext()){
                    String key = keys.next();
                    String value = httpHeader.getString(key);
                    if(value != null) {
                        builder.addHeader(key, value);
                    }
                }
                builder.method(httpMethod, body);
            }
            Request request = builder.build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    promise.setResult(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    promise.setResult(response.body().string());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            promise.setResult(e.getMessage());
        }
    }
}
