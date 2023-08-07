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
     * 获取主机信息
     *
     * @return
     */
    String getHostInfo();
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
     default void disConnect() {}

    /**
     * 发送数据
     *
     * @param content
     */
    void sendMsg(String content);


    /**
     * 是否以16进制发送
     *
     * @param show
     */
    void setIsHexSend(boolean show);


}
