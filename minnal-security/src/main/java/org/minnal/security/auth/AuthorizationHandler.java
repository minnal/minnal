/**
 * 
 */
package org.minnal.security.auth;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

import org.minnal.core.MessageListenerAdapter;
import org.minnal.core.server.MessageContext;
import org.minnal.security.config.SecurityAware;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class AuthorizationHandler extends MessageListenerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationHandler.class);

	@Override
	public void onRouteResolved(MessageContext context) {
		SecurityConfiguration securityConfiguration = null;
		if (context.getApplication().getConfiguration() instanceof SecurityAware) {
			securityConfiguration = ((SecurityAware) context.getApplication().getConfiguration()).getSecurityConfiguration();
		}
		
		if (securityConfiguration == null) {
			logger.debug("Security is not configured for this application. Skipping authorization");
			return;
		}
		Authorizer authorizer = securityConfiguration.getAuthorizer();
		List<Permission> permissions = getPermissions(context.getRoute());
		Principal principal = getPrincipal(context.getRequest());
		if (principal == null) {
			logger.info("User is not authenticated");
			throw new NotAuthorizedException("Not authenticated");
		}
		if (! authorizer.authorize(principal, permissions)) {
			throw new ForbiddenException();
		}
	}
	
	/**
	 * Returns the principal from the current request 
	 * 
	 * @param request
	 * @return
	 */
	protected Principal getPrincipal(Request request) {
		Session session = request.getAttribute(Authenticator.SESSION);
		if (session != null) {
			return (Principal) session.getAttribute(Authenticator.PRINCIPAL);
		}
		return null;
	}
	
	/**
	 * Returns the permissions required for the route
	 * 
	 * @param route
	 * @return
	 */
	protected List<Permission> getPermissions(Route route) {
		String value = route.getAttribute(Authorizer.PERMISSIONS);
		if (Strings.isNullOrEmpty(value)) {
			return new ArrayList<Permission>();
		}
		Iterable<String> permissions = Splitter.on(",").omitEmptyStrings().split(value.toString());
		return Lists.newArrayList(Iterables.transform(permissions, new Function<String, Permission>() {
			@Override
			public Permission apply(String input) {
				return new Permission(input);
			}
		}));
	}
}
