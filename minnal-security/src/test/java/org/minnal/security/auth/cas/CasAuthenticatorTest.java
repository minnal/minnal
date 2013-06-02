/**
 * 
 */
package org.minnal.security.auth.cas;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Collections;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.minnal.security.config.CasConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CasAuthenticatorTest {
	
	private CasAuthenticator authenticator;
	
	private CasConfiguration configuration;
	
	private AbstractPgtTicketStorage ticketStorage;
	
	@BeforeMethod
	public void setup() {
		ticketStorage = mock(AbstractPgtTicketStorage.class);
		configuration = new CasConfiguration("https://localhost:8443", "https://localhost:443/proxyCallback", ticketStorage, new AnyHostnameVerifier());
		authenticator = spy(new CasAuthenticator(configuration));
	}
	
	@Test
	public void shouldGetProxyTicketValidator() {
		TicketValidator validator = authenticator.getValidator();
		assertTrue(validator instanceof Cas20ProxyTicketValidator);
	}
	
	@Test
	public void shouldAuthenticateCredentials() throws TicketValidationException {
		TicketValidator validator = mock(TicketValidator.class);
		Assertion assertion = mock(Assertion.class);
		AttributePrincipal principal = mock(AttributePrincipal.class);
		when(assertion.getPrincipal()).thenReturn(principal);
		when(principal.getName()).thenReturn("testuser");
		when(principal.getAttributes()).thenReturn(Collections.<String, Object>emptyMap());
		when(validator.validate("test123", "http://localhost:8080/orders")).thenReturn(assertion);
		doReturn(validator).when(authenticator).getValidator();
		CasUser user = authenticator.authenticate(new CasCredential("test123", "http://localhost:8080/orders"));
		assertEquals(user.getPrincipal(), principal);
		assertEquals(user.getName(), "testuser");
	}
	
	@Test
	public void shouldReturnNullOnTicketValidationFailure() throws TicketValidationException {
		TicketValidator validator = mock(TicketValidator.class);
		when(validator.validate("test123", "http://localhost:8080/orders")).thenThrow(new TicketValidationException(""));
		doReturn(validator).when(authenticator).getValidator();
		assertNull(authenticator.authenticate(new CasCredential("test123", "http://localhost:8080/orders")));
	}
}
