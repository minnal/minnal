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

/**
 * @author ganeshs
 *
 */
public class InstrumentationBundleTest {
	
	private Container container;
	
	private InstrumentationBundle bundle;
	
	@BeforeMethod
	public void setup() {
		container = mock(Container.class);
		bundle = spy(new InstrumentationBundle());
	}

	@Test
	public void shouldRegisterContainerListenerOnInit() {
		ActiveJpaAgentLoader loader = mock(ActiveJpaAgentLoader.class);
		doReturn(loader).when(bundle).getActiveJpaAgentLoader();
		bundle.init(container);
		verify(loader).loadAgent();
		verify(container).registerListener(bundle);
	}
	
	@Test
	public void shouldEnhanceApplicationOnMount() {
		ApplicationEnhancer enhancer = mock(ApplicationEnhancer.class);
		Application<ApplicationConfiguration> application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(application.getConfiguration()).thenReturn(configuration);
		when(configuration.isInstrumentationEnabled()).thenReturn(true);
		doReturn(enhancer).when(bundle).createApplicationEnhancer(application);
		bundle.postMount(application);
		verify(enhancer).enhance();
	}
	
	@Test
	public void shouldNotEnhanceApplicationIfInstrumentationIsDisabled() {
		ApplicationEnhancer enhancer = mock(ApplicationEnhancer.class);
		Application<ApplicationConfiguration> application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(application.getConfiguration()).thenReturn(configuration);
		doReturn(enhancer).when(bundle).createApplicationEnhancer(application);
		bundle.postMount(application);
		verify(enhancer, never()).enhance();
	}
}
