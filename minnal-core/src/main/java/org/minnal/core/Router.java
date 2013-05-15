/**
 * 
 */
package org.minnal.core;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ganeshs
 *
 */
public class Router {
	
	private RouteResolver resolver;
	
	private static final Logger logger = LoggerFactory.getLogger(Router.class);
	
	public Router(ApplicationMapping applicationMapping) {
		this.resolver = new RouteResolver(applicationMapping);
	}
	
	public Router(RouteResolver resolver) {
		this.resolver = resolver;
	}
	
	public void route(MessageContext context) {
		logger.trace("Routing the context {}", context);
		try {
			Route route = resolver.resolve(context);
			FilterChain chain = new FilterChain(context.getApplication().getFilters(), route);
			chain.doFilter(context.getRequest(), context.getResponse());
		} catch (Exception e) {
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
