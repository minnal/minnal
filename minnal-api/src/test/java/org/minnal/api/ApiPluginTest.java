/**
 * 
 */
package org.minnal.api;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.minnal.api.filter.CorsFilter;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;

/**
 * @author ganeshs
 *
 */
public class ApiPluginTest {

	private ApiPlugin apiPlugin;
	
	private Application<ApplicationConfiguration> application;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
	}

	@Test
	public void shouldAddCorsFilterIfEnabled() {
		apiPlugin = new ApiPlugin(true);
		apiPlugin.init(application);
		verify(application).addFilter(CorsFilter.class);
	}
	
	@Test
	public void shouldNotAddCorsFilterIfNotEnabled() {
		apiPlugin = new ApiPlugin(false);
		apiPlugin.init(application);
		verify(application, never()).addFilter(CorsFilter.class);
	}
	
	@Test
	public void shouldAddProvidersOnInit() {
		apiPlugin = new ApiPlugin(false);
		apiPlugin.init(application);
		verify(application, atLeastOnce()).addProvider(any(ApiDeclarationProvider.class));
		verify(application, atLeastOnce()).addProvider(any(ResourceListingProvider.class));
	}

	@Test
	public void shouldAddResource() {
		apiPlugin = new ApiPlugin(false);
		apiPlugin.init(application);
		verify(application).addResource(ApiListingResourceJSON.class);
	}
	
}
