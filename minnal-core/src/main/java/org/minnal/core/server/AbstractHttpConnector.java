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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractHttpConnector extends SimpleChannelUpstreamHandler implements Lifecycle {
	
	private ServerBootstrap bootstrap;
	
	private Router router;
	
	private ConnectorConfiguration configuration;
	
	private ConnectorListener listener;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractHttpConnector.class);
	
	/**
	 * @param configuration
	 * @param router
	 */
	public AbstractHttpConnector(ConnectorConfiguration configuration, Router router) {
		this.configuration = configuration;
		this.router = router;
	}
	
	public void initialize() {
		logger.info("Initializing the connector");
		if (configuration.getIoWorkerThreadCount() > 0) {
			logger.trace("Creating a server bootstrap with {} io worker threads", configuration.getIoWorkerThreadCount());
			bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool(), configuration.getIoWorkerThreadCount()));
		} else {
			logger.trace("Creating a server bootstrap with default io worker threads");
			bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()));
		}
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline(new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpChunkAggregator(configuration.getMaxContentLength()),
						new HttpContentDecompressor(), new HttpContentCompressor(), AbstractHttpConnector.this);
				addChannelHandlers(pipeline);
				return pipeline;
			}
		});
	}


	
	protected abstract void addChannelHandlers(ChannelPipeline pipeline);

	public void start() {
		logger.info("Starting the connector on the port {}", configuration.getPort());
		bootstrap.bind(new InetSocketAddress(configuration.getPort()));
	}

	public void stop() {
		logger.info("Stopping the connector on the port {}", configuration.getPort());
		bootstrap.shutdown();
	}

	/**
	 * @return the configuration
	 */
	protected ConnectorConfiguration getConfiguration() {
		return configuration;
	}
	
	public void registerListener(ConnectorListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		logger.trace("Received a {} message {} from the remote address {}", configuration.getScheme().name(), e.getMessage(), e.getRemoteAddress());
		ServerRequest request = new ServerRequest((HttpRequest) e.getMessage(), configuration.getScheme().name(), e.getRemoteAddress());
		ServerResponse response = new ServerResponse(request, new DefaultHttpResponse(((HttpRequest) e.getMessage()).getProtocolVersion(), 
				HttpResponseStatus.PROCESSING)); // Setting temp response. Will override while serializing response
		MessageContext context = new MessageContext(request, response);
		ctx.setAttachment(context);
		listener.onReceived(context);
		router.route(context);
		listener.onSuccess(context);
		context.getResponse().write(ctx.getChannel()).addListener(ChannelFutureListener.CLOSE);
		listener.onComplete(context);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		logger.error("Exception caught in the http connector", e);
		if (ctx.getAttachment() instanceof MessageContext) {
			listener.onError((MessageContext) ctx.getAttachment());
		} else {
			listener.onError(e.getCause());
		}
		super.exceptionCaught(ctx, e);
	}
}
