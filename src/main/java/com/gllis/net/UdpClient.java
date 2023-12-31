package com.gllis.net;

import com.gllis.conf.AppConstant;
import com.gllis.util.AppConfUtils;
import com.gllis.util.HexUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UDP 客户端
 *
 * @author gllis
 * @date 2023/7/23
 */
public class UdpClient implements Client {
    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private ClientDispatcher clientDispatcher;

    /**
     * 域名或Ip
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;

    /**
     * 是否使用16进制发送
     */
    private AtomicBoolean isHexSend = new AtomicBoolean(true);
    private final AtomicBoolean started = new AtomicBoolean();

    @Override
    public Client setListener(ClientDispatcher clientDispatcher) {
        this.clientDispatcher = clientDispatcher;
        return this;
    }


    public void create() {
        if (started.compareAndSet(false, true)) {
            this.workerGroup = new NioEventLoopGroup();
            this.bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true);     // 广播
            bootstrap.handler(new ClientHandler());


            try {
                channelFuture = bootstrap.bind(0).sync();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void connect(String host, Integer port) {
        create();
        this.host = host;
        this.port = port;
        AppConfUtils.updateHost(AppConstant.UDP_HOST, host, port);
        clientDispatcher.updateIpArray(AppConfUtils.getHosts(AppConstant.UDP_HOST));
    }


    @Override
    public void sendMsg(String content) {
        if (StringUtil.isNullOrEmpty(content)) {
            clientDispatcher.alertMsg("发送内容为空！");
            return;
        }
        byte[] data = isHexSend.get() ? HexUtil.convertHexToByte(content.trim().toUpperCase())
                : content.trim().getBytes();
        channelFuture.channel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(data),
                new InetSocketAddress(host, port)));

        AppConfUtils.update(AppConstant.UDP_LAST_SEND, content);
        clientDispatcher.receive(channelFuture.channel().localAddress().toString(), data);
    }




    @Override
    public void setIsHexSend(boolean show) {
        this.isHexSend.getAndSet(show);
    }


    class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
            ByteBuf buf = datagramPacket.content();
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            clientDispatcher.receive(datagramPacket.sender().toString(), data);
        }
    }
}
