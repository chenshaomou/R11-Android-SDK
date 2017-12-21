package org.rainboweleven.rbridge.impl;

import org.json.JSONObject;
import org.rainboweleven.rbridge.core.RBridgeEventManager;

/**
 * 事件管理器，管理传递参数为JSONObject类型的事件
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 15:03 GMT+8
 * @email 411086563@qq.com
 */
public class RSystemEventManager extends RBridgeEventManager<JSONObject> {

    // 单例
    private static RSystemEventManager sInstance;

    /**
     * 获取单例
     *
     * @return
     */
    public static RSystemEventManager getInstance() {
        if (sInstance == null) {
            synchronized (RSystemEventManager.class) {
                if (sInstance == null) {
                    sInstance = new RSystemEventManager();
                }
            }
        }
        return sInstance;
    }

    // 构造方法
    private RSystemEventManager() {
    }

}
