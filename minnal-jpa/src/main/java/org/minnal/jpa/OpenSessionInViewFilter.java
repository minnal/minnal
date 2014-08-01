/**
 * 
 */
package org.minnal.jpa;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.minnal.core.config.DatabaseConfiguration;


/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	private DatabaseConfiguration configuration;
	
	private ThreadLocal<Boolean> contextCreated = new ThreadLocal<Boolean>();
	
	public OpenSessionInViewFilter(DatabaseConfiguration configuration) {
		this.configuration = configuration;
	}

	protected JPAContext getContext() {
		return JPA.instance.getDefaultConfig().getContext(configuration.isReadOnly());
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		JPAContext context = getContext();
		context.getEntityManager();
		contextCreated.set(true);
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		if (contextCreated.get() == null) {
			return;
		}
		contextCreated.remove();
		JPAContext context = getContext();
		if (context.isTxnOpen()) {
			context.closeTxn(true);
		}
		context.close();
	}

}
