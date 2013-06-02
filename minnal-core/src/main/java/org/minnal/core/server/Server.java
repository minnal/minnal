/**
 * 
 */
package org.minnal.core.server;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;
import org.minnal.core.config.ServerConfiguration;

/**
 * A simple http server over netty that deletes all the incoming requests to {@link Router}
 * 
 * @author ganeshs
 *
 */
public class Server implements Bundle {

	private ServerConfiguration configuration;
	
	private List<AbstractHttpConnector> connectors = new ArrayList<AbstractHttpConnector>();
	
	public void init(Container container) {
		configuration = container.getConfiguration().getServerConfiguration();
		AbstractHttpConnector connector = null;
		
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
		for (ConnectorConfiguration connectorConfig : configuration.getConnectorConfigurations()) {
			if (connectorConfig.getScheme() == Scheme.https) {
				connector = new HttpsConnector(connectorConfig, container.getRouter());
			} else {
				connector = new HttpConnector(connectorConfig, container.getRouter());
			}
			connector.initialize();
			connectors.add(connector);
		}
	}
	
	public void start() {
		for (AbstractHttpConnector connector : connectors) {
			connector.start();
		}
	}
	
	public void stop() {
		for (AbstractHttpConnector connector : connectors) {
			connector.stop();
		}
	}
}
