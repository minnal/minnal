/**
 * 
 */
package org.minnal.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.server.ServerRequest;
import org.minnal.utils.http.HttpUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ApplicationMappingTest {

	private ApplicationMapping applicationMapping;
	
	private Application<ApplicationConfiguration> application1;
	
	private Application<ApplicationConfiguration> application2;
	
	private ApplicationConfiguration configuration1;
	
	private ApplicationConfiguration configuration2;
	
	@SuppressWarnings("unchecked")
	@BeforeMethod
	public void setup() {
		applicationMapping = new ApplicationMapping("/test");
		application1 = mock(Application.class);
		application2 = mock(Application.class);
		configuration1 = mock(ApplicationConfiguration.class);
		configuration2 = mock(ApplicationConfiguration.class);
		when(application1.getConfiguration()).thenReturn(configuration1);
		when(application2.getConfiguration()).thenReturn(configuration2);
	}
	
	@Test
	public void shouldStructureBasePath() {
		ApplicationMapping mapping = new ApplicationMapping("");
		assertEquals(mapping.getBasePath(), "/");
		mapping = new ApplicationMapping("test");
		assertEquals(mapping.getBasePath(), "/test");
		mapping = new ApplicationMapping("test/");
		assertEquals(mapping.getBasePath(), "/test");
		mapping = new ApplicationMapping("test/test1/");
		assertEquals(mapping.getBasePath(), "/test/test1");
	}
	
	@Test
	public void shouldAddApplication() {
		when(configuration1.getBasePath()).thenReturn("/app1");
		when(configuration2.getBasePath()).thenReturn("/app2");
		applicationMapping.addApplication(application1);
		applicationMapping.addApplication(application2);
		assertEquals(applicationMapping.getApplications().size(), 2);
	}
	
	@Test
	public void shouldSetApplicationPathWhenAdded() {
		when(configuration1.getBasePath()).thenReturn("/app");
		applicationMapping.addApplication(application1);
		verify(application1).setPath("/test/app");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAddApplicationIfMountPathIsAlreadyUsed() {
		when(configuration1.getBasePath()).thenReturn("/app");
		when(configuration2.getBasePath()).thenReturn("/app");
		applicationMapping.addApplication(application1);
		applicationMapping.addApplication(application2);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAddApplicationIfApplicationIsAlreadyAdded() {
		when(configuration1.getBasePath()).thenReturn("/app1");
		when(configuration1.getBasePath()).thenReturn("/app2");
		applicationMapping.addApplication(application1);
		applicationMapping.addApplication(application1);
	}
	
	@Test
	public void shouldRemoveApplicationByMountPath() {
		when(configuration1.getBasePath()).thenReturn("/app");
		applicationMapping.addApplication(application1);
		applicationMapping.removeApplication("/app");
		assertTrue(applicationMapping.getApplications().isEmpty());
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldThrowExceptionIfApplicationIsAlreadyRemoved() {
		when(configuration1.getBasePath()).thenReturn("/app");
		applicationMapping.addApplication(application1);
		applicationMapping.removeApplication("/app");
		applicationMapping.removeApplication("/app");
	}
	
	@Test
	public void shouldResolveRequestToApplication() {
		when(configuration1.getBasePath()).thenReturn("/app1");
		when(configuration2.getBasePath()).thenReturn("/app2");
		applicationMapping.addApplication(application1);
		applicationMapping.addApplication(application2);
		ServerRequest request = mock(ServerRequest.class);
		when(request.getUri()).thenReturn(HttpUtil.createURI("/test/app1/test123"));
		assertEquals(applicationMapping.resolve(request), application1);
	}
	
	@Test
	public void shouldNotResolveRequestToApplication() {
		when(configuration1.getBasePath()).thenReturn("/app1");
		applicationMapping.addApplication(application1);
		ServerRequest request = mock(ServerRequest.class);
		when(request.getUri()).thenReturn(HttpUtil.createURI("/test/invalidapp/test123"));
		assertNull(applicationMapping.resolve(request));
	}
		
}
