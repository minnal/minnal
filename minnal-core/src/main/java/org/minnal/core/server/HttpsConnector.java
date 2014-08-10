/**
 * 
 */
package org.minnal.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.minnal.core.MinnalException;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.SSLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class HttpsConnector extends AbstractHttpConnector {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpsConnector.class);
	
	/**
	 * @param configuration
	 * @param router
	 */
	public HttpsConnector(ConnectorConfiguration configuration, Router router) {
		super(configuration, router);
		if (configuration.getSslConfiguration() == null) {
			logger.error("SSL configuration is missing for https connector");
			throw new MinnalException("SSL configuration is missing for https scheme");
		}
	}

	@Override
	protected void addChannelHandlers(ChannelPipeline pipeline) {
		logger.debug("Adding ssl handler to the pipeline");
		SSLEngine engine = createSslEngine();
		engine.setUseClientMode(false);
		pipeline.addFirst("ssl", new SslHandler(engine));
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.trace("Performing a handshake on channel connect");
		final SslHandler sslHandler = ctx.pipeline().get(SslHandler.class);
		Future<Channel> future = sslHandler.handshakeFuture();
		future.addListener(new FutureListener<Channel>() {
			@Override
			public void operationComplete(Future<Channel> future) throws Exception {
				future.get().close();
			}
		});
	}
	
	/**
	 * @return
	 */
	protected SSLEngine createSslEngine() {
		logger.debug("Creating a SSL engine from the SSL context");
		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
			logger.trace("ssl.KeyManagerFactory.algorithm algorithm is not set. Defaulting to {}", algorithm);
		}
		SSLContext serverContext = null;
		SSLConfiguration configuration = getConnectorConfiguration().getSslConfiguration();
		InputStream stream = null;
		try {
			File file = new File(configuration.getKeyStoreFile());
			stream = new FileInputStream(file);
			KeyStore ks = KeyStore.getInstance(configuration.getKeystoreType());
			ks.load(stream, configuration.getKeyStorePassword().toCharArray());

			// Set up key manager factory to use our key store
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(ks, configuration.getKeyPassword().toCharArray());

			// Initialize the SSLContext to work with our key managers.
			serverContext = SSLContext.getInstance(configuration.getProtocol());
			serverContext.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			logger.error("Failed while initializing the ssl context", e);
			throw new MinnalException("Failed to initialize the ssl context", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					logger.trace("Failed while closing the stream", e);
				}
			}
 		}
		return serverContext.createSSLEngine();
	}
}
