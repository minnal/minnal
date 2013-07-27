/**
 * 
 */
package org.minnal.core.server;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.minnal.core.Container;
import org.minnal.core.ContainerMessageObserver;
import org.minnal.core.Router;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;
import org.minnal.core.config.ContainerConfiguration;
import org.minnal.core.config.SSLConfiguration;
import org.minnal.core.config.ServerConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ServerTest {
	
	private Container container;
	
	private HttpConnector httpConnector;
	
	private HttpsConnector httpsConnector;
	
	private Router router;
	
	private Server server;
	
	private ContainerMessageObserver messageObserver;
	
	@BeforeMethod
	public void setup() {
		server = spy(new Server());
		container = mock(Container.class);
		messageObserver = mock(ContainerMessageObserver.class);
		when(container.getMessageObserver()).thenReturn(messageObserver);
		router = mock(Router.class);
		when(container.getRouter()).thenReturn(router);
		ContainerConfiguration configuration = mock(ContainerConfiguration.class);
		ServerConfiguration serverConfiguration = mock(ServerConfiguration.class);
		ConnectorConfiguration connectorConfig1 = mock(ConnectorConfiguration.class);
		when(connectorConfig1.getScheme()).thenReturn(Scheme.http);
		ConnectorConfiguration connectorConfig2 = mock(ConnectorConfiguration.class);
		when(connectorConfig2.getScheme()).thenReturn(Scheme.https);
		when(connectorConfig2.getSslConfiguration()).thenReturn(mock(SSLConfiguration.class));
		when(serverConfiguration.getConnectorConfigurations()).thenReturn(Arrays.asList(connectorConfig1, connectorConfig2));
		when(configuration.getServerConfiguration()).thenReturn(serverConfiguration);
		when(container.getConfiguration()).thenReturn(configuration);
		httpConnector = mock(HttpConnector.class);
		httpsConnector = mock(HttpsConnector.class);
		doReturn(httpConnector).when(server).createHttpConnector(connectorConfig1, router);
		doReturn(httpsConnector).when(server).createHttpsConnector(connectorConfig2, router);
	}
	
	@Test
	public void shouldInitializeServer() {
		server.init(container, null);
		verify(httpConnector).registerListener(messageObserver);
		verify(httpConnector).initialize();
		verify(httpsConnector).registerListener(messageObserver);
		verify(httpsConnector).initialize();
	}
	
	@Test
	public void shouldStartAllConnectors() {
		server.init(container, null);
		server.start();
		verify(httpConnector).start();
		verify(httpsConnector).start();
	}
	
	@Test
	public void shouldStopAllConnectors() {
		server.init(container, null);
		server.stop();
		verify(httpConnector).stop();
		verify(httpsConnector).stop();
	}
	
	@Test
	public void shouldGetDefaultOrder() {
		assertEquals(server.getOrder(), Integer.MAX_VALUE);
	}
}
