/**
 * 
 */
package org.minnal.instrument;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.instrument.resource.ResourceEnhancer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ApplicationEnhancerTest {
	
	private Application<ApplicationConfiguration> application;
	
	private ResourceClass rc1;
	
	private ResourceClass rc2;
	
	private ApplicationEnhancer enhancer;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
		rc1 = mock(ResourceClass.class);
		rc2 = mock(ResourceClass.class);
		when(application.getResources()).thenReturn(Arrays.asList(rc1, rc2));
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getPackagesToScan()).thenReturn(Arrays.asList("org.minnal.instrument"));
		when(application.getConfiguration()).thenReturn(configuration);
		enhancer = spy(new ApplicationEnhancer(application));
		ResourceEnhancer resEnhancer = mock(ResourceEnhancer.class);
		doReturn(resEnhancer).when(enhancer).createEnhancer(any(ResourceClass.class));
	}

	@Test
	public void shouldEnhanceResourceClass() {
		ResourceEnhancer enhancer1 = mock(ResourceEnhancer.class);
		ResourceEnhancer enhancer2 = mock(ResourceEnhancer.class);
		doReturn(enhancer1).when(enhancer).createEnhancer(rc1);
		doReturn(enhancer2).when(enhancer).createEnhancer(rc2);
		enhancer.enhance();
		verify(enhancer1).enhance();
		verify(enhancer2).enhance();
	}
}
