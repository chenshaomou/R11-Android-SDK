package org.rainboweleven.rbridge.impl.core_plugin;

import org.json.JSONObject;
import org.rainboweleven.rbridge.impl.BaseAsyncPlugin;

/**
 * @author andy(Andy)
 * @datetime 2017-12-20 21:52 GMT+8
 * @email 411086563@qq.com
 */
public class NetworkPlugin extends BaseAsyncPlugin {

    @Override
    public void onPluginCalled(String module, String method, JSONObject params, OnCallPluginListener listener) {
        if (!"network".equals(module)) {
            return;
        }
        try {

        } catch (Exception e) {

        }
    }
}
