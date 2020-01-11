package com.netty.udp.unicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * 作者：DarkKIng
 * 类说明：订阅服务器
 */
public class UdpQuestionSide {

    public final static String QUESTION = "我想听个笑话";

    public void run(int port) throws Exception{

        EventLoopGroup group  = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    /*由于我们用的是UDP协议，所以要用NioDatagramChannel来创建*/
                    .channel(NioDatagramChannel.class)
                    .handler(new QuestoinHandler());
            //不需要建立连接
            Channel ch = b.bind(0).sync().channel();
            //将UDP请求的报文以DatagramPacket打包发送给接受端
            ch.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer(QUESTION,
                                    CharsetUtil.UTF_8),
                            new InetSocketAddress("127.0.0.1",
                                    port)))
                    .sync();
            //不知道接收端能否收到报文，也不知道能否收到接收端的应答报文
            // 所以等待15秒后，不再等待，关闭通信
            if(!ch.closeFuture().await(15000)){
                System.out.println("查询超时！");
            }
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }
    public static void main(String [] args) throws Exception{
        int answerPort = 8080;

        new UdpQuestionSide().run(answerPort);
    }
}
