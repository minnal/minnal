/**
 * 
 */
package org.minnal.security;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.minnal.security.auth.Authorizer;
import org.minnal.security.auth.User;
import org.minnal.security.filter.AuthenticationFilter;
import org.minnal.security.session.Session;
import org.pac4j.http.profile.HttpProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class MinnalSecurityContextTest {

	private MinnalSecurityContext context;
	
	private Authorizer authorizer;
	
	private Session session;
	
	@BeforeMethod
	public void setup() {
		authorizer = mock(Authorizer.class);
		session = mock(Session.class);
		context = spy(new MinnalSecurityContext(authorizer, session));
	}
	
	@Test
	public void shouldGetPrincipal() {
		HttpProfile profile = mock(HttpProfile.class);
		when(session.getAttribute(AuthenticationFilter.PRINCIPAL)).thenReturn(profile);
		User user = context.getUserPrincipal();
		assertEquals(user.getProfile(), profile);
	}
	
	@Test
	public void shouldReturnNullIfPrincipalNotFound() {
		when(session.getAttribute(AuthenticationFilter.PRINCIPAL)).thenReturn(null);
		User user = context.getUserPrincipal();
		assertNull(user);
	}
	
	@Test
	public void shouldReturnTrueIfTheUserHasRole() {
		User user = mock(User.class);
		doReturn(user).when(context).getUserPrincipal();
		when(authorizer.authorize(user, "role1")).thenReturn(true);
		assertTrue(context.isUserInRole("role1"));
	}
	
	@Test
	public void shouldReturnTrueIfTheUserDoesntHaveRole() {
		User user = mock(User.class);
		doReturn(user).when(context).getUserPrincipal();
		when(authorizer.authorize(user, "role1")).thenReturn(false);
		assertFalse(context.isUserInRole("role1"));
	}
}
