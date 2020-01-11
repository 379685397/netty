package com.netty.udp.broadcast.acceptside;

import com.netty.udp.broadcast.LogConst;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * 作者：DarkKIng
 * 类说明：日志的接受端
 */
public class LogEventMonitor {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        //引导该 NioDatagramChannel
        bootstrap.group(group)
            .channel(NioDatagramChannel.class)
            //设置套接字选项 SO_BROADCAST
            .option(ChannelOption.SO_BROADCAST, true)
            //允许重用
            .option(ChannelOption.SO_REUSEADDR,true)
            .handler( new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel)
                    throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new LogEventDecoder());
                    pipeline.addLast(new LogEventHandler());
                }
            } )
            .localAddress(address);
    }

    public Channel bind() {
        //绑定 Channel。注意，DatagramChannel 是无连接的
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        //构造一个新的 UdpAnswerSide并指明监听端口
        LogEventMonitor monitor = new LogEventMonitor(
            new InetSocketAddress(LogConst.MONITOR_SIDE_PORT));
        try {
            //绑定本地监听端口
            Channel channel = monitor.bind();
            System.out.println("UdpAnswerSide running");
            channel.closeFuture().sync();
        } finally {
            monitor.stop();
        }
    }
}
