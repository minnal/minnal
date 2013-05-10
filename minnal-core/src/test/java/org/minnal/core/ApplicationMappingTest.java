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

import java.net.URI;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.util.HttpUtil;
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
	
	@SuppressWarnings("unchecked")
	@BeforeMethod
	public void setup() {
		applicationMapping = new ApplicationMapping("/test");
		application1 = mock(Application.class);
		application2 = mock(Application.class);
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
		applicationMapping.addApplication(application1, "/app1");
		applicationMapping.addApplication(application2, "/app2");
		assertEquals(applicationMapping.getApplications().size(), 2);
	}
	
	@Test
	public void shouldSetApplicationPathWhenAdded() {
		applicationMapping.addApplication(application1, "/app");
		verify(application1).setPath("/test/app");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAddApplicationIfMountPathIsAlreadyUsed() {
		applicationMapping.addApplication(application1, "/app");
		applicationMapping.addApplication(application2, "/app");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAddApplicationIfApplicationIsAlreadyAdded() {
		applicationMapping.addApplication(application1, "/app1");
		applicationMapping.addApplication(application1, "/app2");
	}
	
	@Test
	public void shouldRemoveApplicationByMountPath() {
		applicationMapping.addApplication(application1, "/app");
		applicationMapping.removeApplication("/app");
		assertTrue(applicationMapping.getApplications().isEmpty());
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldThrowExceptionIfApplicationIsAlreadyRemoved() {
		applicationMapping.addApplication(application1, "/app");
		applicationMapping.removeApplication("/app");
		applicationMapping.removeApplication("/app");
	}
	
	@Test
	public void shouldResolveRequestToApplication() {
		applicationMapping.addApplication(application1, "/app1");
		applicationMapping.addApplication(application2, "/app2");
		ServerRequest request = mock(ServerRequest.class);
		when(request.getUri()).thenReturn(HttpUtil.createURI("/test/app1/test123"));
		assertEquals(applicationMapping.resolve(request), application1);
	}
	
	@Test
	public void shouldNotResolveRequestToApplication() {
		applicationMapping.addApplication(application1, "/app1");
		ServerRequest request = mock(ServerRequest.class);
		when(request.getUri()).thenReturn(HttpUtil.createURI("/test/invalidapp/test123"));
		assertNull(applicationMapping.resolve(request));
	}
		
}
