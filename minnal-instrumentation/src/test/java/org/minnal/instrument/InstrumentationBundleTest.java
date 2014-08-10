/**
 * 
 */
package org.minnal.instrument;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.minnal.core.Application;
import org.minnal.core.Container;
import org.minnal.core.config.ApplicationConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author ganeshs
 *
 */
public class InstrumentationBundleTest {
	
	private Container container;
	
	private InstrumentationBundle bundle;
	
	private Application<ApplicationConfiguration> application;
	
	private MinnalApplicationEnhancer enhancer;
	
	@BeforeMethod
	public void setup() {
		container = mock(Container.class);
		bundle = spy(new InstrumentationBundle());
		enhancer = mock(MinnalApplicationEnhancer.class);
		application = mock(Application.class);
		when(application.getObjectMapper()).thenReturn(mock(ObjectMapper.class));
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(application.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void shouldRegisterContainerListenerOnInit() {
		ActiveJpaAgentLoader loader = mock(ActiveJpaAgentLoader.class);
		doReturn(loader).when(bundle).getActiveJpaAgentLoader();
		bundle.init(container, null);
		verify(loader).loadAgent();
		verify(container).registerListener(bundle);
	}
	
	@Test
	public void shouldEnhanceApplicationOnMount() {
		ApplicationConfiguration configuration = application.getConfiguration();
		when(configuration.isInstrumentationEnabled()).thenReturn(true);
		doReturn(enhancer).when(bundle).createApplicationEnhancer(application);
		bundle.postMount(application);
		verify(enhancer).enhance();
	}
	
	@Test
	public void shouldNotEnhanceApplicationIfInstrumentationIsDisabled() {
		doReturn(enhancer).when(bundle).createApplicationEnhancer(application);
		bundle.preMount(application);
		verify(enhancer, never()).enhance();
	}
}
