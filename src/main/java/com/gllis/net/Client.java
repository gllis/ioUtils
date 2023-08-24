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
    default void connect(String host, Integer port) {};

    /**
     * 连接
     *
     * @param host
     * @param port
     * @param clientId
     */
    default void connect(String host, Integer port, String clientId) {};

    /**
     * 断开连接
     */
     default void disConnect() {}

    /**
     * 发送数据
     *
     * @param content
     */
    default void sendMsg(String content) {}

    /**
     * 发送数据
     *
     * @param topic
     * @param content
     */
    default void sendMsg(String topic, String content) {}


    /**
     * 是否以16进制发送
     *
     * @param show
     */
    default void setIsHexSend(boolean show) {};


}
