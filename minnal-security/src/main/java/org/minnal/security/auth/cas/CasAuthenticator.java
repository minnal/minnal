/**
 * 
 */
package org.minnal.security.auth.cas;

import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.minnal.security.auth.Authenticator;
import org.minnal.security.config.CasConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class CasAuthenticator implements Authenticator<CasCredential, CasUser> {
	
	private CasConfiguration configuration;
	
	private TicketValidator validator;
	
	private static final Logger logger = LoggerFactory.getLogger(CasAuthenticator.class);
	
	public CasAuthenticator(CasConfiguration configuration) {
		this.configuration = configuration;
	}

	public CasUser authenticate(CasCredential credential) {
		try {
			Assertion assertion = getValidator().validate(credential.getTicket(), credential.getServiceUrl());
			return new CasUser(assertion.getPrincipal());
		} catch (TicketValidationException e) {
			logger.warn("Ticket validation failed for the credential - " + credential, e);
			return null;
		}
	}
	
	protected TicketValidator getValidator() {
		if (validator == null) {
			Cas20ProxyTicketValidator validator = new Cas20ProxyTicketValidator(configuration.getCasServerUrl());
			validator.setProxyCallbackUrl(configuration.getCasProxyCallbackUrl());
			validator.setHostnameVerifier(configuration.getHostnameVerifier());
			validator.setProxyGrantingTicketStorage(configuration.getTicketStorage());
			this.validator = validator;
		}
		return validator;
	}
}
