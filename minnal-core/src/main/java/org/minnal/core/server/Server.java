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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple http server over netty that delegates all the incoming requests to {@link Router}
 * 
 * @author ganeshs
 *
 */
public class Server implements Bundle<ServerBundleConfiguration> {

	private List<AbstractHttpConnector> connectors = new ArrayList<AbstractHttpConnector>();
	
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	public void init(Container container, ServerBundleConfiguration config) {
		logger.info("Initializing the container");
		// Override the supplied one
		ServerConfiguration configuration = container.getConfiguration().getServerConfiguration();
		AbstractHttpConnector connector = null;
		
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
		
		logger.info("Loading the http connectors");
		for (ConnectorConfiguration connectorConfig : configuration.getConnectorConfigurations()) {
			if (connectorConfig.getScheme() == Scheme.https) {
				connector = createHttpsConnector(connectorConfig, container.getRouter());
			} else {
				connector = createHttpConnector(connectorConfig, container.getRouter());
			}
			connector.registerListener(container.getMessageObserver());
			connector.initialize();
			connectors.add(connector);
		}
	}
	
	public void start() {
		logger.info("Starting the connectors");
		for (AbstractHttpConnector connector : connectors) {
			connector.start();
		}
	}
	
	public void stop() {
		logger.info("Stopping the connectors");
		for (AbstractHttpConnector connector : connectors) {
			connector.stop();
		}
	}
	
	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}
	
	protected HttpConnector createHttpConnector(ConnectorConfiguration connectorConfig, Router router) {
		return new HttpConnector(connectorConfig, router);
	}
	
	protected HttpsConnector createHttpsConnector(ConnectorConfiguration connectorConfig, Router router) {
		return new HttpsConnector(connectorConfig, router);
	}
}
