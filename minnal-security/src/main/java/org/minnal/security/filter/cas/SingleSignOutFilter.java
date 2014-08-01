/**
 * 
 */
package org.minnal.security.filter.cas;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;

import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class SingleSignOutFilter implements Filter {
	
	private SecurityConfiguration configuration;
	
	private static final Logger logger = LoggerFactory.getLogger(SingleSignOutFilter.class);
	
	private static final Pattern LOGOUT_MESSAGE_PATTERN = Pattern.compile("^<samlp:LogoutRequest.*?<samlp:SessionIndex>(.*)</samlp:SessionIndex>.*?", Pattern.DOTALL);
	
	public SingleSignOutFilter() {
	}

	/**
	 * @param configuration
	 */
	public SingleSignOutFilter(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	public void doFilter(Request request, Response response, FilterChain chain) {
		String logoutMessage = request.getHeader(configuration.getCasConfiguration().getLogoutParameterName());
		if (request.getHttpMethod().equals(HttpMethod.POST) && ! Strings.isNullOrEmpty(logoutMessage)) {
			logger.trace("Processing logout request - {}", logoutMessage);
			
			// Double decode the message
			try {
				logoutMessage = URLDecoder.decode(logoutMessage, Charsets.UTF_8.name());
				logoutMessage = URLDecoder.decode(logoutMessage, Charsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
				logger.error("Failed while decoding the logout message - {}", logoutMessage);
				throw new MinnalException(e);
			}
			
			String token = null;
			Matcher matcher = LOGOUT_MESSAGE_PATTERN.matcher(logoutMessage);
			if (matcher.find()) {
				token = matcher.group(1);
			} else {
				logger.error("Couldn't retrieve the token from logout message - {}", logoutMessage);
				throw new MinnalException("Invalid logout message");
			}
			
			if (! configuration.getCasConfiguration().isEnableSingleSignout()) {
				logger.trace("Single sign out disabled. Ignoring the logout requets for the token {}", token);
			} else {
				Session session = configuration.getSessionStore().findSessionBy("serviceTicket", token);
				if (session != null) {
					configuration.getSessionStore().deleteSession(session.getId());
				} else {
					logger.warn("Session not found for the token - {}", token);
				}
			}
			response.setStatus(HttpResponseStatus.OK);
		} else {
			logger.trace("Not a logout request. Forwarding to the next filter");
			chain.doFilter(request, response);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configuration == null) ? 0 : configuration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleSignOutFilter other = (SingleSignOutFilter) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		return true;
	}
}
