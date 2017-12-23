package org.rainboweleven.rbridge.impl;

/**
 * 事件管理器
 *
 * @author andy(Andy)
 * @datetime 2017-12-21 09:35 GMT+8
 * @email 411086563@qq.com
 */
public class RBridgeEventManager {

    private static RBridgeEventManager sInstance;

    /**
     * 获取单例
     *
     * @return
     */
    public static RBridgeEventManager getInstance() {
        if (sInstance == null) {
            synchronized (RBridgeEventManager.class) {
                if (sInstance == null) {
                    sInstance = new RBridgeEventManager();
                }
            }
        }
        return sInstance;
    }

    // 构造方法
    private RBridgeEventManager() {
    }

    // 注册事件
    public void register(String eventName, Object params) {

    }

    // 取消注册事件
    public void unregister(String eventName) {

    }

    // 发送事件
    public void send(String eventName, Object params) {

    }
}
