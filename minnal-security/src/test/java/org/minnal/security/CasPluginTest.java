/**
 * 
 */
package org.minnal.security;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.minnal.core.Application;
import org.minnal.core.resource.ResourceClass;
import org.minnal.security.auth.cas.AbstractPgtTicketStorage;
import org.minnal.security.config.CasConfiguration;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.cas.CasFilter;
import org.minnal.security.filter.cas.CasProxyCallbackFilter;
import org.minnal.security.session.SessionStore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CasPluginTest {
	
	private SecurityConfiguration configuration;
	
	private Application application;
	
	private CasPlugin plugin;
	
	@BeforeMethod
	public void setup() {
		CasConfiguration casConfiguration = new CasConfiguration("https://localhost:8443", "https://localhost:443/proxyCallback", 
				mock(AbstractPgtTicketStorage.class), new AnyHostnameVerifier());
		configuration = new SecurityConfiguration(casConfiguration, mock(SessionStore.class), 100);
		application = mock(Application.class);
		plugin = new CasPlugin(configuration);
	}

	@Test
	public void shouldAddFiltersOnInit() {
		plugin.init(application);
		verify(application).addFilter(new CasFilter(configuration));
		verify(application).addFilter(new CasProxyCallbackFilter(configuration));
	}
	
	@Test
	public void shouldAddResourceOnInit() {
		plugin.init(application);
		verify(application).addResource(any(ResourceClass.class));
	}
}