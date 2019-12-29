package com.netty.fileserver;

import com.netty.fileserver.FileServerHandle;
import com.netty.fileserver.Pipeline;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 作者：DarkKIng
 * 创建日期：2019/12/17
 * 类说明：文件下载服务端
 */
public class FileServer {

    private final int port;

    public FileServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        int port = 9999;
        FileServer fileServer = new FileServer(port);
        System.out.println("服务器即将启动");
        fileServer.start();
        System.out.println("服务器关闭");
    }

    public void start() throws InterruptedException {
        final FileServerHandle serverHandler = new FileServerHandle();
        /*线程组*/
        EventLoopGroup group = new NioEventLoopGroup();
        Pipeline pipeline = new Pipeline();
        try {
            /*服务端启动必须*/
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)/*将线程组传入*/
                    .channel(NioServerSocketChannel.class)/*指定使用NIO进行网络传输*/
                    .localAddress(new InetSocketAddress(port))/*指定服务器监听端口*/
                    /*服务端每接收到一个连接请求，就会新启一个socket通信，也就是channel，
                    所以下面这段代码的作用就是为这个子channel增加handle*/
                    .childHandler(pipeline);
            ChannelFuture f = b.bind().sync();/*异步绑定到服务器，sync()会阻塞直到完成*/
            System.out.println("Netty server  start,port is " + port);
            f.channel().closeFuture().sync();/*阻塞直到服务器的channel关闭*/

        } finally {
            group.shutdownGracefully().sync();/*优雅关闭线程组*/
        }

    }


}

