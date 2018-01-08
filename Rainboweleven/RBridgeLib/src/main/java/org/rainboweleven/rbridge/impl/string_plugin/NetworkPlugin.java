package org.rainboweleven.rbridge.impl.string_plugin;

import android.util.Log;

import org.rainboweleven.rbridge.impl.base_plugin.BaseStringAsyncPlugin;

/**
 * 网络插件，传递String参数，返回String结果
 *
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class NetworkPlugin extends BaseStringAsyncPlugin {

    public static final String MODULE_NAME = "network";

    @Override
    public void onPluginCalled(String module, String method, String params, final OnCallPluginListener<String> listener) {
        // Demo测试
        String result = "NetworkPlugin(StringPlugin)被调用";
        try {
            result = result + "，module：" + module + "，method：" + method + "，params：" + params;
            Log.e("wlf", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listener != null) {
            listener.onCallPluginResult(result);
            listener.onCallPluginFinish();
        }

        /**
        // 具体实现
        try {
            if(!MODULE_NAME.equals(module)){
                return null;
            }

            JSONObject jsonParams = new JSONObject(params);
            String url = jsonParams.optString("url");
            String httpMethod = jsonParams.optString("method");
            JSONObject httpParams = jsonParams.optJSONObject("param");

            OkHttpClient okHttpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url);
            if("post".equalsIgnoreCase(httpMethod)){
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), httpParams.toString());
                builder.method(httpMethod, body);
            }
            Request request = builder.build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(listener != null){
                        listener.onCallPluginResult("异常,e:"+e);
                        listener.onCallPluginFinish();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(listener != null){
                        listener.onCallPluginResult(response.body().string());
                        listener.onCallPluginFinish();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            if(listener != null){
                listener.onCallPluginResult("异常,e:"+e);
                listener.onCallPluginFinish();
            }
        }
         */
    }
}
