package com.gllis.net;

public interface ClientDispatcher {

    /**
     * 接收数据
     *
     * @param address
     * @param data
     */
    void receive(String address, byte[] data);

    /**
     * 已连接
     */
    void connected();

    /**
     * 断开连接
     */
    void disConnect();

    /**
     * 系统报错提示
     *
     * @param msg
     */
    void alertMsg(String msg);

    /**
     * 更新ip列表
     * @param ipArray
     */
    void updateIpArray(String[] ipArray);
}
