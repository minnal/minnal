/**
 * 
 */
package org.minnal.api;

import java.net.InetAddress;

import org.minnal.core.Application;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.ContainerAdapter;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;

/**
 * @author ganeshs
 *
 */
public class ApiBundle extends ContainerAdapter implements Bundle<ApiBundleConfiguration> {

	@Override
	public void init(Container container, ApiBundleConfiguration configuration) {
		container.registerListener(this);
		for (ConnectorConfiguration connector : container.getConfiguration().getServerConfiguration().getConnectorConfigurations()) {
			ApiDocumentation.instance.setBaseUrl(connector.getScheme().toString() + "://" + getHostName() + ":" + connector.getPort());
			if (connector.getScheme() == Scheme.http) {
				break;
			}
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void postMount(Application<ApplicationConfiguration> application) {
		ApiDocumentation.instance.addApplication(application);
	}
	
	@Override
	public int getOrder() {
		return Integer.MAX_VALUE - 1;
	}
	
	protected String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			return "localhost";
		}
	}
}
