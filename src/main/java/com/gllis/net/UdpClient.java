package com.gllis.net;

public class UdpClient implements Client {

    private ClientDispatcher clientDispatcher;

    @Override
    public Client setListener(ClientDispatcher clientDispatcher) {
        this.clientDispatcher = clientDispatcher;
        return this;
    }

    @Override
    public void connect(String host, Integer port) {

    }

    @Override
    public void disConnect() {

    }

    @Override
    public void sendMsg(String content) {

    }

    @Override
    public void destroy() {

    }
}
