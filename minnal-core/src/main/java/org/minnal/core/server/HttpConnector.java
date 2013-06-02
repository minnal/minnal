/**
 * 
 */
package org.minnal.core.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;

/**
 * @author ganeshs
 *
 */
public class HttpConnector extends AbstractHttpConnector {

	public HttpConnector(ConnectorConfiguration configuration, Router router) {
		super(configuration, router);
	}
	
	@Override
	protected void addChannelHandlers(ChannelPipeline pipeline) {
	}
}
