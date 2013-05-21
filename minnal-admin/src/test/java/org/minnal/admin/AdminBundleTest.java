/**
 * 
 */
package org.minnal.admin;

import static org.mockito.Mockito.*;
import org.minnal.core.Container;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class AdminBundleTest {
	
	@Test
	public void shouldRegisterListenerOnInit() {
		AdminBundle bundle = new AdminBundle();
		Container container = mock(Container.class);
		bundle.init(container);
		verify(container).registerListener(bundle);
	}

}
