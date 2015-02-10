/**
 * 
 */
package org.minnal.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.URI;

import org.minnal.core.Lifecycle;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.utils.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
@Sharable
public abstract class AbstractHttpConnector extends SimpleChannelInboundHandler<FullHttpRequest> implements Lifecycle {
	
	private ServerBootstrap bootstrap;
	
	private Router router;
	
	private ConnectorConfiguration configuration;
	
	private ConnectorListener listener;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractHttpConnector.class);
	
	public static final String REQUEST_PROPERTY_REMOTE_ADDR = "org.minnal.container.netty.request.property.remote_addr";
	
	public static final AttributeKey<MessageContext> MESSAGE_CONTEXT = AttributeKey.valueOf("org.minnal.message_context");
	
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
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(configuration.getIoWorkerThreadCount());
	    EventLoopGroup workerGroup = new NioEventLoopGroup(configuration.getIoWorkerThreadCount());
	    bootstrap = new ServerBootstrap();
	    bootstrap.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 100)
        .childOption(ChannelOption.TCP_NODELAY, true)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpRequestDecoder(), new HttpResponseEncoder(), new HttpObjectAggregator(configuration.getMaxContentLength()), AbstractHttpConnector.this);
                addChannelHandlers(ch.pipeline());
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
		bootstrap.group().shutdownGracefully();
		bootstrap.childGroup().shutdownGracefully();
		try {
			bootstrap.group().terminationFuture().sync();
		} catch (InterruptedException e) {
			logger.warn("Failed while stopping the boss threads", e);
		}
		try {
			bootstrap.childGroup().terminationFuture().sync();
		} catch (InterruptedException e) {
			logger.warn("Failed while stopping the worker threads", e);
		}
	}

	/**
	 * @return the configuration
	 */
	protected ConnectorConfiguration getConnectorConfiguration() {
		return configuration;
	}
	
	public void registerListener(ConnectorListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		logger.error("Exception caught in the http connector", e);
		if (ctx.attr(MESSAGE_CONTEXT).get() instanceof MessageContext) {
			listener.onError(ctx.attr(MESSAGE_CONTEXT).get());
		} else {
			listener.onError(e.getCause());
		}
		super.exceptionCaught(ctx, e);
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception {
		logger.trace("Received a {} message {} from the remote address {}", configuration.getScheme().name(), httpRequest, ctx.channel().remoteAddress());
		URI baseUri = HttpUtil.createURI(configuration.getScheme().name(), httpRequest.headers().get(HttpHeaders.Names.HOST), "//");
		MessageContext context = new MessageContext(httpRequest, baseUri);
		ctx.attr(MESSAGE_CONTEXT).set(context);
		listener.onReceived(context);
		router.route(context);
		listener.onSuccess(context);
		ctx.writeAndFlush(context.getResponse()).addListener(ChannelFutureListener.CLOSE);
		listener.onComplete(context);
	}
}
