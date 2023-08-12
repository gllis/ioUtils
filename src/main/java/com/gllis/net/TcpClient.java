package com.gllis.net;


import com.gllis.conf.AppConstant;
import com.gllis.util.AppConfUtils;
import com.gllis.util.HexUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.StringUtil;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TCP 客户端
 *
 * @author GL
 * @date 2023/7/27
 */
public class TcpClient implements Client {

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private ClientDispatcher clientDispatcher;

    private String host;
    private Integer port;

    /**
     * 是否使用16进制发送
     */
    private AtomicBoolean isHexSend = new AtomicBoolean(true);

    @Override
    public TcpClient setListener(ClientDispatcher clientDispatcher) {
        this.clientDispatcher = clientDispatcher;
        return this;
    }

    @Override
    public String getHostInfo() {
        return MessageFormat.format("{0}:{1}", host, String.valueOf(port));
    }

    public TcpClient create() {
        this.workerGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                initPipeline(ch.pipeline());
            }
        });
        initOptions(bootstrap);
        return this;
    }

    @Override
    public void connect(String host, Integer port) {
        try {
            this.host = host;
            this.port = port;
            channelFuture = bootstrap.connect(host, port).sync();
            AppConfUtils.updateHost(AppConstant.TCP_HOST, host, port);
            clientDispatcher.updateIpArray(AppConfUtils.getHosts(AppConstant.TCP_HOST));
        } catch (Exception e) {
            e.printStackTrace();
            clientDispatcher.alertMsg("请求连接服务器失败！");
        }
    }

    @Override
    public void disConnect() {
        try {
            if (channelFuture == null) {
                return;
            }
            channelFuture.channel().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMsg(String content) {
        if (StringUtil.isNullOrEmpty(content)) {
            clientDispatcher.alertMsg("发送内容为空！");
            return;
        }
        if (channelFuture == null || !channelFuture.channel().isActive()) {
            clientDispatcher.alertMsg("请先连接服务器！");
            return;
        }
        try {
            byte[] data = isHexSend.get() ? HexUtil.convertHexToByte(content.trim().toUpperCase())
                    : content.trim().getBytes();
            channelFuture.channel().writeAndFlush(data);
            AppConfUtils.update(AppConstant.TCP_LAST_SEND, content);
        } catch (Exception e) {
            e.printStackTrace();
            clientDispatcher.alertMsg("发送失败！");
        }
    }




    @Override
    public void setIsHexSend(boolean show) {
        this.isHexSend.getAndSet(show);
    }

    private void initPipeline(ChannelPipeline pipeline) {
        ClientHandler clientHandler = new ClientHandler();
        pipeline.addLast(new IdleStateHandler(10, 0, 10, TimeUnit.MINUTES));
        pipeline.addLast(new ByteArrayEncoder());
        pipeline.addLast(new ByteArrayDecoder());
        pipeline.addLast(clientHandler);
    }


    private void initOptions(Bootstrap b) {
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 4000);
        b.option(ChannelOption.TCP_NODELAY, true);
    }

    class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            byte[] data = (byte[]) msg;
            clientDispatcher.receive(data);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            clientDispatcher.connected();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            clientDispatcher.disConnect();
        }
    }
}
