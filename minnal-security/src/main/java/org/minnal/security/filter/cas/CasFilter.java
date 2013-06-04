/**
 * 
 */
package org.minnal.security.filter.cas;

import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.SeeOtherException;
import org.minnal.security.auth.cas.CasAuthenticator;
import org.minnal.security.auth.cas.CasCredential;
import org.minnal.security.auth.cas.CasUser;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.AbstractAuthenticationFilter;
import org.minnal.security.session.JpaSession;
import org.minnal.security.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class CasFilter extends AbstractAuthenticationFilter<CasCredential, CasUser, CasAuthenticator> {
	
	private CasAuthenticator authenticator;
	
	private static final Logger logger = LoggerFactory.getLogger(CasFilter.class);
	
	public CasFilter(SecurityConfiguration configuration) {
		super(configuration);
		authenticator = new CasAuthenticator(configuration.getCasConfiguration());
	}
	
	@Override
	protected CasAuthenticator getAuthenticator() {
		return authenticator;
	}

	@Override
	protected CasCredential getCredential(Request request) {
		String ticket = request.getHeader("ticket");
		if (ticket == null) {
			logger.debug("Ticket is not found in the request. Redirecting to cas server");
			throw new SeeOtherException(constructRedirectUrl(request));
		}
		return new CasCredential(ticket, request.getUri().toASCIIString().split("\\?")[0]);
	}
	
	@Override
	protected void handleAuthSuccess(Request request, Response response, Session session) {
		((JpaSession) session).setServiceTicket(request.getHeader("ticket"));
		super.handleAuthSuccess(request, response, session);
	}
	
	private String constructRedirectUrl(Request request) {
		return getConfiguration().getCasConfiguration().getCasServerUrl() + "/login?service=" + request.getUri().toASCIIString(); 
	}
}