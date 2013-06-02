/**
 * 
 */
package org.minnal.core.server;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.handler.ssl.SslHandler;
import org.minnal.core.MinnalException;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.SSLConfiguration;

/**
 * @author ganeshs
 *
 */
public class HttpsConnector extends AbstractHttpConnector {
	
	/**
	 * @param configuration
	 * @param router
	 */
	public HttpsConnector(ConnectorConfiguration configuration, Router router) {
		super(configuration, router);
		if (configuration.getSslConfiguration() == null) {
			throw new MinnalException("SSL configuration is missing for https scheme");
		}
	}

	@Override
	protected void addChannelHandlers(ChannelPipeline pipeline) {
		SSLEngine engine = createSslEngine();
		engine.setUseClientMode(false);
		pipeline.addFirst("ssl", new SslHandler(engine));
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// Get the SslHandler in the current pipeline.
		// We added it in SecureChatPipelineFactory.
		final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
		
		// Get notified when SSL handshake is done.
		ChannelFuture future = sslHandler.handshake();
		future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	}
	
	/**
	 * @return
	 */
	protected SSLEngine createSslEngine() {
		String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
		if (algorithm == null) {
			algorithm = "SunX509";
		}
		SSLContext serverContext = null;
		SSLConfiguration configuration = getConfiguration().getSslConfiguration();
		try {
			KeyStore ks = KeyStore.getInstance(configuration.getKeystoreType());
			File file = new File(configuration.getKeyStoreFile());
			ks.load(new FileInputStream(file), configuration.getKeyStorePassword().toCharArray());

			// Set up key manager factory to use our key store
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
			kmf.init(ks, configuration.getKeyPassword().toCharArray());

			// Initialize the SSLContext to work with our key managers.
			serverContext = SSLContext.getInstance(configuration.getProtocol());
			serverContext.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			throw new MinnalException("Failed to initialize the ssl context", e);
		}
		return serverContext.createSSLEngine();
	}
}
