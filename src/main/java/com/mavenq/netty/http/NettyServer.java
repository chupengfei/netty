package com.mavenq.netty.http;

import com.mavenq.netty.http.handle.TestHttpServerHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitialize());

            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }



    }


    static class ChannelInitialize extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("httpServerCodec", new HttpServerCodec());
            pipeline.addLast("testHttpServerHandle", new TestHttpServerHandle());
        }
    }
}
