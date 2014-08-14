/**
 * 
 */
package org.minnal.api;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.minnal.api.filter.JacksonModelConvertor;
import org.minnal.api.filter.MinnalApiSpecFilter;
import org.minnal.core.Application;
import org.minnal.core.Container;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;
import org.minnal.core.config.ContainerConfiguration;
import org.minnal.core.config.ServerConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.FilterFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.converter.ModelConverter;
import com.wordnik.swagger.converter.ModelConverters;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;

/**
 * @author ganeshs
 *
 */
public class ApiBundleTest {

	private ApiBundle apiBundle;
	
	private Container container;
	
	private ApiBundleConfiguration bundleConfiguration;
	
	@BeforeMethod
	public void setup() {
		apiBundle = spy(new ApiBundle());
		container = mock(Container.class);
		ContainerConfiguration configuration = mock(ContainerConfiguration.class);
		when(container.getConfiguration()).thenReturn(configuration);
		ServerConfiguration serverConfiguration = mock(ServerConfiguration.class);
		when(configuration.getServerConfiguration()).thenReturn(serverConfiguration);
		ConnectorConfiguration connectorConfiguration = new ConnectorConfiguration(8080, Scheme.http, null, 2);
		when(serverConfiguration.getConnectorConfigurations()).thenReturn(Lists.newArrayList(connectorConfiguration));
		bundleConfiguration = new ApiBundleConfiguration(true, Lists.<Class<? extends Annotation>>newArrayList(JsonIgnore.class));
	}
	
	@Test
	public void shouldRegisterContainerListenerOnInit() {
		apiBundle.init(container, bundleConfiguration);
		verify(container).registerListener(apiBundle);
	}
	
	@Test
	public void shouldConfigureSwagger() {
		ModelConverter converter = mock(JacksonModelConvertor.class);
		doReturn(converter).when(apiBundle).getModelConvertor();
		doReturn("localhost").when(apiBundle).getHostName();
		apiBundle.init(container, bundleConfiguration);
		SwaggerConfig config = ConfigFactory.config();
		assertEquals(config.apiVersion(), "1.0.1");
		assertEquals(config.basePath(), "http://localhost:8080");
		assertTrue(ScannerFactory.scanner().get() instanceof DefaultJaxrsScanner);
		assertTrue(ClassReaders.reader().get() instanceof DefaultJaxrsApiReader);
		assertTrue(FilterFactory.filter() instanceof MinnalApiSpecFilter);
		ModelConverters.addConverter(converter, true);
	}

	@Test
	public void shouldGetLocalHost() throws UnknownHostException {
		assertEquals(apiBundle.getHostName(), InetAddress.getLocalHost().getHostAddress());
	}
	
	@Test
	public void shouldGetModelConverter() {
		apiBundle.init(container, bundleConfiguration);
		JacksonModelConvertor convertor = apiBundle.getModelConvertor();
		assertEquals(convertor.getExcludeAnnotations(), Lists.newArrayList(JsonBackReference.class, JsonIgnore.class));
	}
	
	@Test
	public void shouldRegisterPluginOnPreMount() {
		Application<ApplicationConfiguration> application = mock(Application.class);
		apiBundle.init(container, bundleConfiguration);
		apiBundle.preMount(application);
		verify(application).registerPlugin(any(ApiPlugin.class));
	}
}
