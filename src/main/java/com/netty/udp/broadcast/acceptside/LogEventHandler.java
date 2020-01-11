package com.netty.udp.broadcast.acceptside;

import com.netty.udp.broadcast.LogMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 作者：DarkKIng
 * 类说明：日志的业务处理类,实际的业务处理，接受日志信息
 */
public class LogEventHandler
    extends SimpleChannelInboundHandler<LogMsg> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) throws Exception {
        //当异常发生时，打印栈跟踪信息，并关闭对应的 Channel
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
        LogMsg event) throws Exception {
        //创建 StringBuilder，并且构建输出的字符串
        StringBuilder builder = new StringBuilder();
        builder.append(event.getTime());
        builder.append(" [");
        builder.append(event.getSource().toString());
        builder.append("] ：[");
        builder.append(event.getMsgId());
        builder.append("] ：");
        builder.append(event.getMsg());
        //打印 LogMsg 的数据
        System.out.println(builder.toString());
    }
}
