package com.gllis.net;

/**
 * 客户端接口
 *
 * @author gllis
 * @date 2023/7/26
 */
public interface Client {

    Client setListener(ClientDispatcher clientDispatcher);

    /**
     * 连接
     *
     * @param host
     * @param port
     */
    void connect(String host, Integer port);

    /**
     * 断开连接
     */
    void disConnect();

    /**
     * 发送数据
     *
     * @param content
     */
    void sendMsg(String content);

    /**
     * 销毁客户端
     */
    void destroy();
}
