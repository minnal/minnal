/**
 * 
 */
package org.minnal.api;

import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.minnal.api.filter.ExcludeAnnotationsConvertor;
import org.minnal.api.filter.MinnalApiSpecFilter;
import org.minnal.core.Application;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.ContainerAdapter;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.collect.Lists;
import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.FilterFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.converter.ModelConverters;
import com.wordnik.swagger.converter.OverrideConverter;
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
		ModelConverters.addConverter(getExcludeAnnotationsConvertor(), true);
		ModelConverters.addConverter(getOverrideConverter(), true);
	}
	
	/**
	 * Returns the override converter
	 * 
	 * @return
	 */
	protected OverrideConverter getOverrideConverter() {
		OverrideConverter converter = new OverrideConverter();
		String dateJson = "{\"id\": \"date-time\", \"name\": \"date-time\", \"qualifiedType\": \"date-time\"}";
		converter.add(Date.class.getCanonicalName(), dateJson);
		converter.add(Timestamp.class.getCanonicalName(), dateJson);
		return converter;
	}
	
	/**
	 * Returns the model convertor
	 * 
	 * @return
	 */
	protected ExcludeAnnotationsConvertor getExcludeAnnotationsConvertor() {
		List<Class<? extends Annotation>> excludedAnnotations = Lists.<Class<? extends Annotation>>newArrayList(JsonBackReference.class);
		excludedAnnotations.addAll(configuration.getExcludedAnnotations());
		return new ExcludeAnnotationsConvertor(excludedAnnotations);
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
