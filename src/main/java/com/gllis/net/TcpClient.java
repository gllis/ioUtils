package com.gllis.net;


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

import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

    @Override
    public TcpClient setListener(ClientDispatcher clientDispatcher) {
        this.clientDispatcher = clientDispatcher;
        return this;
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
            channelFuture = bootstrap.connect(host, port).sync();
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
            byte[] bytes = HexUtil.convertHexToByte(content);
            channelFuture.channel().writeAndFlush(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            clientDispatcher.alertMsg("发送失败！");
        }
    }

    @Override
    public void destroy() {
        try {
            if (channelFuture != null && channelFuture.channel().isActive()) {
                channelFuture.channel().close().syncUninterruptibly();
            }
            workerGroup.shutdownGracefully().syncUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            byte[] bytes = (byte[]) msg;
            clientDispatcher.receive(HexUtil.convertByteToHex(bytes).toUpperCase(Locale.ROOT));
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
