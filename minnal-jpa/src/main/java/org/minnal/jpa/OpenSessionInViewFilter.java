/**
 * 
 */
package org.minnal.jpa;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.minnal.core.Filter;
import org.minnal.core.FilterChain;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.DatabaseConfiguration;

/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilter implements Filter {
	
	private DatabaseConfiguration configuration;
	
	public OpenSessionInViewFilter(DatabaseConfiguration configuration) {
		this.configuration = configuration;
	}

	public void doFilter(Request request, Response response, FilterChain chain) {
		JPAContext context = getContext();
		context.getEntityManager(); // Initialize an entity manager for this request
		try {
			chain.doFilter(request, response);
		} finally {
			if (context.isTxnOpen()) {
				context.closeTxn(true);
			}
			context.close();
		}
	}
	
	protected JPAContext getContext() {
		return JPA.instance.getDefaultConfig().getContext(configuration.isReadOnly());
	}

}
