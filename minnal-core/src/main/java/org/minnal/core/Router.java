/**
 * 
 */
package org.minnal.core;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.exception.ApplicationException;
import org.minnal.core.server.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ganeshs
 *
 */
public class Router {
	
	private RouteResolver resolver;
	
	private ApplicationMapping applicationMapping;
	
	private static final Logger logger = LoggerFactory.getLogger(Router.class);
	
	public Router(ApplicationMapping applicationMapping) {
		this.applicationMapping = applicationMapping;
		this.resolver = new RouteResolver(applicationMapping);
	}
	
	public Router(ApplicationMapping applicationMapping, RouteResolver resolver) {
		this.resolver = resolver;
		this.applicationMapping = applicationMapping;
	}
	
	public void route(MessageContext context) {
		logger.trace("Routing the context {}", context);
		
		Application<ApplicationConfiguration> application = applicationMapping.resolve(context.getRequest());
		if (application == null) {
			throw new NotFoundException("Request path not found");
		}
		context.setApplication(application);
		
		try {
			FilterChain chain = new FilterChain(context.getApplication().getFilters(), resolver);
			chain.doFilter(context);
		} catch (Exception e) {
			if (! (e instanceof ApplicationException)) {
				logger.error("Failed while processing the request");
			}
			context.getApplication().getExceptionHandler().handle(context.getRequest(), context.getResponse(), e);
		} finally {
			if (context.getResponse().getStatus() == HttpResponseStatus.PROCESSING) {
				if (context.getResponse().isContentSet()) {
					context.getResponse().setStatus(HttpResponseStatus.OK);
				} else {
					context.getResponse().setStatus(HttpResponseStatus.NO_CONTENT);
				}
			}
		}
	}
}
