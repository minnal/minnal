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
			Object result = route.getAction().invoke(context.getRequest(), context.getResponse());
			if (result != null && ! context.getResponse().isContentSet()) {
				context.getResponse().setContent(result);
			}
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
