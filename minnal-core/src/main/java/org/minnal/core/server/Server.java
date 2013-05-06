/**
 * 
 */
package org.minnal.core.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.Router;
import org.minnal.core.config.ServerConfiguration;

/**
 * @author ganeshs
 *
 */
public class Server extends SimpleChannelUpstreamHandler implements Bundle {

	private ServerBootstrap bootstrap;
	
	private Router router;
	
	private ServerConfiguration configuration;
	
	public void init(Container container) {
		configuration = container.getConfiguration().getServerConfiguration();
		router = container.getRouter();
		if (configuration.getIoWorkerThreadCount() > 0) {
			bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool(), configuration.getIoWorkerThreadCount()));
		} else {
			bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
		}
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(), new ChunkedWriteHandler(), 
						new HttpContentDecompressor(), new HttpContentCompressor(), Server.this);
			}
		});
	}
	
	public void start() {
		bootstrap.bind(new InetSocketAddress(configuration.getHttpPort()));
	}
	
	public void stop() {
		bootstrap.shutdown();
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ServerRequest request = new ServerRequest((HttpRequest) e.getMessage(), e.getRemoteAddress());
		ServerResponse response = new ServerResponse(new DefaultHttpResponse(((HttpRequest) e.getMessage()).getProtocolVersion(), 
				HttpResponseStatus.PROCESSING)); // Setting temp response. Will override while serializing response
		MessageContext context = new MessageContext(request, response);
		ctx.setAttachment(context);
		router.route(context);
		context.getResponse().write(ctx.getChannel()).addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		super.exceptionCaught(ctx, e);
	}
}
