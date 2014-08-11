/**
 * 
 */
package org.minnal.security.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.minnal.security.MinnalSecurityContext;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;

/**
 * @author ganeshs
 *
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class SecurityContextFilter extends AbstractSecurityFilter implements ContainerRequestFilter {
	
	/**
	 * @param configuration
	 */
	public SecurityContextFilter(SecurityConfiguration configuration) {
		super(configuration);
	}

	@Override
	public void filter(ContainerRequestContext request) {
		Session session = getSession(request, true);
		MinnalSecurityContext context = new MinnalSecurityContext(getConfiguration().getAuthorizer(), session);
		request.setSecurityContext(context);
	}

}
