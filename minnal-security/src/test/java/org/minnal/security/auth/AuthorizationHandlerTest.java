/**
 * 
 */
package org.minnal.security.auth;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.minnal.core.server.exception.ForbiddenException;
import org.minnal.core.server.exception.UnauthorizedException;
import org.minnal.security.config.SecurityAware;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class AuthorizationHandlerTest {
	
	private MessageContext context;
	
	private Application<ApplicationConfiguration> application;
	
	private ServerRequest request;
	
	private ServerResponse response;
	
	private AuthorizationHandler handler;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
		request = mock(ServerRequest.class);
		response = mock(ServerResponse.class);
		context = new MessageContext(request, response);
		context.setApplication(application);
		handler = spy(new AuthorizationHandler());
	}

	@Test
	public void shouldSkipAuthorizationIfApplicationIsNotConfiguredForAuth() {
		ApplicationConfiguration applicationConfiguration = new DummyConfiguration();
		when(application.getConfiguration()).thenReturn(applicationConfiguration);
		handler.onRouteResolved(context);
	}
	
	@Test(expectedExceptions=UnauthorizedException.class, expectedExceptionsMessageRegExp="Not authenticated")
	public void shouldThrowUnauthenticatedIfSessionNotFound() {
		DummyConfiguration applicationConfiguration = new DummyConfiguration();
		SecurityConfiguration securityConfiguration = mock(SecurityConfiguration.class);
		applicationConfiguration.setSecurityConfiguration(securityConfiguration);
		Authorizer authorizer = mock(Authorizer.class);
		when(securityConfiguration.getAuthorizer()).thenReturn(authorizer);
		when(application.getConfiguration()).thenReturn(applicationConfiguration);
		doReturn(Arrays.asList(new Role("role1"))).when(handler).getPermissions(any(Route.class));
		doReturn(null).when(handler).getPrincipal(request);
		handler.onRouteResolved(context);
	}
	
	@Test
	public void shouldAuthorizeIfRoleMatchesThePrincipal() {
		DummyConfiguration applicationConfiguration = new DummyConfiguration();
		SecurityConfiguration securityConfiguration = new SecurityConfiguration();
		applicationConfiguration.setSecurityConfiguration(securityConfiguration);
		when(application.getConfiguration()).thenReturn(applicationConfiguration);
		List<Permission> permissions = Arrays.asList(new Permission("permission1"));
		doReturn(permissions).when(handler).getPermissions(any(Route.class));
		User principal = mock(User.class);
		when(principal.hasPermissions(permissions)).thenReturn(true);
		doReturn(principal).when(handler).getPrincipal(request);
		handler.onRouteResolved(context);
	}
	
	@Test(expectedExceptions=ForbiddenException.class)
	public void shouldAuthorizeIfRoleDoesntMatchThePrincipal() {
		DummyConfiguration applicationConfiguration = new DummyConfiguration();
		SecurityConfiguration securityConfiguration = new SecurityConfiguration();
		applicationConfiguration.setSecurityConfiguration(securityConfiguration);
		when(application.getConfiguration()).thenReturn(applicationConfiguration);
		List<Permission> permissions = Arrays.asList(new Permission("permission1"));
		doReturn(permissions).when(handler).getPermissions(any(Route.class));
		User principal = mock(User.class);
		when(principal.hasPermissions(permissions)).thenReturn(false);
		doReturn(principal).when(handler).getPrincipal(request);
		handler.onRouteResolved(context);
	}
	
	@Test
	public void shouldGetPrincipal() {
		Session session = mock(Session.class);
		Principal principal = mock(Principal.class);
		when(session.getAttribute(Authenticator.PRINCIPAL)).thenReturn(principal);
		when(request.getAttribute(Authenticator.SESSION)).thenReturn(session);
		assertEquals(handler.getPrincipal(request), principal);
	}
	
	@Test
	public void shouldNotGetPrincipalIfSessionNotFound() {
		when(request.getAttribute(Authenticator.SESSION)).thenReturn(null);
		assertNull(handler.getPrincipal(request));
	}
	
	@Test
	public void shouldGetPermissionsFromRoute() {
		Route route = mock(Route.class);
		when(route.getAttribute(Authorizer.PERMISSIONS)).thenReturn("permission1,permission2");
		List<Permission> permissions = handler.getPermissions(route);
		assertEquals(permissions.size(), 2);
		assertEquals(permissions, Arrays.asList(new Permission("permission1"), new Permission("permission2")));
	}
	
	@Test
	public void shouldGetEmptyPermissionsFromRoute() {
		Route route = mock(Route.class);
		List<Permission> permissions = handler.getPermissions(route);
		assertEquals(permissions.size(), 0);
	}
	
	public static class DummyConfiguration extends ApplicationConfiguration implements SecurityAware {
		private SecurityConfiguration securityConfiguration;
		@Override
		public SecurityConfiguration getSecurityConfiguration() {
			return securityConfiguration;
		}

		@Override
		public void setSecurityConfiguration(SecurityConfiguration configuration) {
			this.securityConfiguration = configuration;
		}
	}
}
