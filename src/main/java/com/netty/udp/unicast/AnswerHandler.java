package com.netty.udp.unicast;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.Random;

/**
 * 作者：DarkKIng
 * 类说明：应答处理Handler
 */
public class AnswerHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {

    /*应答的具体内容从常量字符串数组中取得，由nextQuote方法随机获取*/
    private static final String[] DICTIONARY = {
            "一个男生暗恋一个女生很久了。一天自习课上，男生偷偷的传了小纸条给女生，上面写着“其实我注意你很久了”。不一会儿，女生传了另一张纸条，男生心急火燎的打开一看“拜托你不要告诉老师，我保证以后再也不嗑瓜子了”。。。。。。男生一脸懵逼",
            "昨天因为一件事骂儿子，说你妈妈是猪，你也是头猪。儿子却反过来说我：爸爸你怎么这么衰，娶了一头猪，还生了一只猪！你说你这熊孩子，这是不是找打。",
            "火云邪神苦练多年，终于将蛤蟆功练至顶级并成功产下8个小蝌蚪。",
            "老婆永远是对的，这话永远也是对的。但老婆没想到的是，她不一定永远是老婆",
            "人生天地间没有谁是容易的，就算是思聪也得每天犯愁怎么花钱。",
    "今天去理发，洗剪吹68，烫发和染发668。我就做了个洗剪吹，结账的时候发现居然收我66"};
    private static Random r = new Random();
    private String nextQuote(){
        return DICTIONARY[r.nextInt(DICTIONARY.length-1)];
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                DatagramPacket packet)
            throws Exception {

        //获得请求
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println("接收到请求："+req);
        if(UdpQuestionSide.QUESTION.equals(req)){
            String answer = UdpAnswerSide.ANSWER+nextQuote();
            System.out.println("接收到请求："+req);
            /**
             * 重新 new 一个DatagramPacket对象，我们通过packet.sender()来获取发送者的消息。重新发送出去！
             */
            ctx.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer(
                                    answer,
                                    CharsetUtil.UTF_8),
                            packet.sender()));
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
