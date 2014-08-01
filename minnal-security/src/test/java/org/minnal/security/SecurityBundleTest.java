/**
 * 
 */
package org.minnal.security;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.minnal.core.Container;
import org.minnal.security.auth.AuthorizationHandler;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SecurityBundleTest {

	@Test
	public void shouldRegisterMessageListenerOnInit() {
		Container container = mock(Container.class);
		SecurityBundle bundle = new SecurityBundle();
		bundle.init(container, mock(SecurityBundleConfiguration.class));
		verify(container).registerListener(any(AuthorizationHandler.class));
	}
}
