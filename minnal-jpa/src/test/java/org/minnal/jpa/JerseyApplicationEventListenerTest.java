/**
 * 
 */
package org.minnal.jpa;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JerseyApplicationEventListenerTest {

	@Test
	public void shouldReturnOpenSessionInViewFilterOnRequestEvent() {
		DatabaseConfiguration configuration = mock(DatabaseConfiguration.class);
		JerseyApplicationEventListener listener = new JerseyApplicationEventListener(configuration);
		RequestEvent requestEvent = mock(RequestEvent.class);
		RequestEventListener eventListener = listener.onRequest(requestEvent);
		assertTrue(eventListener instanceof OpenSessionInViewFilter);
		assertEquals(((OpenSessionInViewFilter)eventListener).getConfiguration(), configuration);
	}

}
