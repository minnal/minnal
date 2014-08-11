/**
 * 
 */
package org.minnal.security.filter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import javax.ws.rs.container.ContainerRequestContext;

import org.minnal.security.MinnalSecurityContext;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SecurityContextFilterTest {

	private SecurityContextFilter filter;
	
	private SecurityConfiguration configuration;
	
	private ContainerRequestContext context;
	
	@BeforeMethod
	public void setup() {
		configuration = mock(SecurityConfiguration.class);
		filter = spy(new SecurityContextFilter(configuration));
		context = mock(ContainerRequestContext.class);
	}
	
	@Test
	public void shouldSetSecurityContextToTheRequest() {
		Session session = mock(Session.class);
		doReturn(session).when(filter).getSession(context, true);
		filter.filter(context);
		verify(context).setSecurityContext(any(MinnalSecurityContext.class));
	}
}
