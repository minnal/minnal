/**
 * 
 */
package org.minnal.security;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.security.config.SecurityAware;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.AuthenticationFilter;
import org.minnal.security.filter.CallbackFilter;
import org.minnal.security.filter.SecurityContextFilter;
import org.pac4j.core.client.Client;
import org.pac4j.http.client.BasicAuthClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SecurityPluginTest {

	private SecurityPlugin plugin;
	
	private String callbackUrl = "/callback";
	
	private Client[] clients = new Client[] {new BasicAuthClient()};
	
	@BeforeMethod
	public void setup() {
		plugin = new SecurityPlugin(callbackUrl, clients);
	}
	
	@Test
	public void shouldInitPlugin() {
		Application application = mock(Application.class);
		SecureApplicationConfiguration securityAware = mock(SecureApplicationConfiguration.class);
		ResourceConfig resourceConfig = mock(ResourceConfig.class);
		when(application.getConfiguration()).thenReturn(securityAware);
		when(application.getResourceConfig()).thenReturn(resourceConfig);
		plugin.init(application);
		verify(application, atLeast(1)).addFilter(any(SecurityContextFilter.class));
		verify(application, atLeast(1)).addFilter(any(AuthenticationFilter.class));
		verify(application, atLeast(1)).addFilter(any(CallbackFilter.class));
		verify(resourceConfig).register(RolesAllowedDynamicFeature.class);
	}
	
	private static class SecureApplicationConfiguration extends ApplicationConfiguration implements SecurityAware {

		@Override
		public SecurityConfiguration getSecurityConfiguration() {
			return null;
		}

		@Override
		public void setSecurityConfiguration(SecurityConfiguration configuration) {
		}
		
	}
}
