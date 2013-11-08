/**
 * 
 */
package org.minnal.security.filter.basic;

import java.util.List;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.security.auth.User;
import org.minnal.security.auth.basic.AbstractBasicAuthenticator;
import org.minnal.security.auth.basic.BasicCredential;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.AbstractAuthenticationFilter;
import org.minnal.security.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;

/**
 * @author ganeshs
 *
 */
public class BasicAuthenticationFilter extends AbstractAuthenticationFilter<BasicCredential, User, AbstractBasicAuthenticator> {
	
	private AbstractBasicAuthenticator authenticator;
	
	private static final String PREFIX = "Basic ";
	
	private static final Logger logger = LoggerFactory.getLogger(BasicAuthenticationFilter.class);

	/**
	 * @param authenticator
	 * @param configuration
	 */
	public BasicAuthenticationFilter(AbstractBasicAuthenticator authenticator, SecurityConfiguration configuration) {
		super(configuration);
		this.authenticator = authenticator;
	}

	@Override
	protected BasicCredential getCredential(Request request) {
		String headerValue = request.getHeader(HttpHeaders.Names.AUTHORIZATION);
		BasicCredential credential = null;
		try {
			if (! Strings.isNullOrEmpty(headerValue) && headerValue.indexOf(PREFIX) == 0) {
				String token = new String(BaseEncoding.base64().decode(headerValue.substring(PREFIX.length())));
				List<String> creds = Lists.newArrayList(Splitter.on(":").split(token));
				if (creds.size() == 2) {
					credential = new BasicCredential(creds.get(0), creds.get(1));
				}
			}
		} catch (Exception e) {
			logger.info("Failed while parsing the authorization header", e);
		}
		return credential;
	}

	@Override
	protected AbstractBasicAuthenticator getAuthenticator() {
		return authenticator;
	}
	
	protected void handleAuthFailure(Request request, Response response, Session session) {
		response.addHeader(HttpHeaders.Names.WWW_AUTHENTICATE, PREFIX + "realm=" + getConfiguration().getRealm());
		super.handleAuthFailure(request, response, session);
	}

}
