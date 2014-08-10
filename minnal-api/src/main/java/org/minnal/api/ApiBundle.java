/**
 * 
 */
package org.minnal.api;

import java.net.InetAddress;

import org.minnal.api.filter.JacksonModelConvertor;
import org.minnal.api.filter.MinnalApiSpecFilter;
import org.minnal.core.Application;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.ContainerAdapter;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.FilterFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.converter.ModelConverters;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;

/**
 * @author ganeshs
 * 
 */
public class ApiBundle extends ContainerAdapter implements Bundle<ApiBundleConfiguration> {
	
	private ApiBundleConfiguration configuration;

	@Override
	public void init(Container container, ApiBundleConfiguration configuration) {
		this.configuration = configuration;
		container.registerListener(this);
		
		SwaggerConfig config = ConfigFactory.config();
		config.setApiVersion("1.0.1");
		
		for (ConnectorConfiguration connector : container.getConfiguration().getServerConfiguration().getConnectorConfigurations()) {
			if (connector.getScheme() == Scheme.http) {
				config.setBasePath(connector.getScheme().toString() + "://" + getHostName() + ":" + connector.getPort());
				break;
			}
		}
		
		ScannerFactory.setScanner(new DefaultJaxrsScanner());
		ClassReaders.setReader(new DefaultJaxrsApiReader());
		FilterFactory.setFilter(new MinnalApiSpecFilter());
		ModelConverters.addConverter(new JacksonModelConvertor(), true);
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void preMount(Application<ApplicationConfiguration> application) {
		 application.registerPlugin(new ApiPlugin(configuration.isEnableCors()));
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
