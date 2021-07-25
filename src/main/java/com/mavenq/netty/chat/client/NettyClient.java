package com.mavenq.netty.chat.client;

import com.mavenq.netty.chat.client.handle.MyChatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NettyClient {

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
            ChannelFuture channelFuture
                    = bootstrap.connect("localhost", 8899).sync();
            Channel channel = channelFuture.channel();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            for(;;){
               channel.writeAndFlush(bufferedReader.readLine() + "\r\n");
            }

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }


    static class ClientInitializer extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0 ,4));
            pipeline.addLast(new LengthFieldPrepender(4));
            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
            pipeline.addLast(new MyChatClientHandler());
        }
    }

}
