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
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Lifecycle;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractHttpConnector extends SimpleChannelUpstreamHandler implements Lifecycle {
	
	private ServerBootstrap bootstrap;
	
	private Router router;
	
	private ConnectorConfiguration configuration;
	
	/**
	 * @param configuration
	 * @param router
	 */
	public AbstractHttpConnector(ConnectorConfiguration configuration, Router router) {
		this.configuration = configuration;
		this.router = router;
	}
	
	public void initialize() {
		if (configuration.getIoWorkerThreadCount() > 0) {
			bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool(), configuration.getIoWorkerThreadCount()));
		} else {
			bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
		}
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpChunkAggregator(65536), 
						new HttpContentDecompressor(), new HttpContentCompressor(), AbstractHttpConnector.this);
				addChannelHandlers(pipeline);
				return pipeline;
			}
		});
	}
	
	protected abstract void addChannelHandlers(ChannelPipeline pipeline);

	public void start() {
		bootstrap.bind(new InetSocketAddress(configuration.getPort()));
	}

	public void stop() {
		bootstrap.shutdown();
	}

	/**
	 * @return the configuration
	 */
	public ConnectorConfiguration getConfiguration() {
		return configuration;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ServerRequest request = new ServerRequest((HttpRequest) e.getMessage(), configuration.getScheme().name(), e.getRemoteAddress());
		ServerResponse response = new ServerResponse(request, new DefaultHttpResponse(((HttpRequest) e.getMessage()).getProtocolVersion(), 
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
