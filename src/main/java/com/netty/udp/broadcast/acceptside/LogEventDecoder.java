package com.netty.udp.broadcast.acceptside;

import com.netty.udp.broadcast.LogMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.List;

/**
 * 作者：DarkKIng
 * 类说明：解码，将DatagramPacket解码为实际的日志实体类
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          DatagramPacket datagramPacket, List<Object> out)
        throws Exception {
        //获取对 DatagramPacket 中的数据（ByteBuf）的引用
        ByteBuf data = datagramPacket.content();
        long time = new Date().getTime();

        System.out.println(time+" 接受到发送的消息:");
        //获得消息的id
        long msgId = data.readLong();
        //获得分隔符SEPARATOR
        byte sepa = data.readByte();
        //获取读索引的当前位置，就是分隔符的索引+1
        int idx = data.readerIndex();
        //提取日志消息，从读索引开始，到最后为日志的信息
        String sendMsg = data.slice(idx ,
            data.readableBytes()).toString(CharsetUtil.UTF_8);
        //构建一个新的 LogMsg 对象，并且将它添加到（已经解码的消息的）列表中
        LogMsg event = new LogMsg(datagramPacket.sender(),
                msgId, sendMsg);
        //作为本handler的处理结果，交给后面的handler进行处理
        out.add(event);
    }
}
