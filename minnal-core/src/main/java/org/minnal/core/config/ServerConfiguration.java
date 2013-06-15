/**
 * 
 */
package org.minnal.core.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public class ServerConfiguration {

	@JsonProperty("connectors")
	private List<ConnectorConfiguration> connectorConfigurations = new ArrayList<ConnectorConfiguration>();
	
	public ServerConfiguration() {
	}

	/**
	 * @param connectorConfigurations
	 */
	public ServerConfiguration(List<ConnectorConfiguration> connectorConfigurations) {
		this.connectorConfigurations = connectorConfigurations;
	}

	/**
	 * @return the connectorConfigurations
	 */
	public List<ConnectorConfiguration> getConnectorConfigurations() {
		return connectorConfigurations;
	}

	/**
	 * @param connectorConfigurations the connectorConfigurations to set
	 */
	public void setConnectorConfigurations(
			List<ConnectorConfiguration> connectorConfigurations) {
		this.connectorConfigurations = connectorConfigurations;
	}

	public void addConnectorConfiguration(ConnectorConfiguration configuration) {
		this.connectorConfigurations.add(configuration);
	}
}
