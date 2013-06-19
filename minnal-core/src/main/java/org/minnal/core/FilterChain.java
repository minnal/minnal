/**
 * 
 */
package org.minnal.core;

import java.util.Iterator;
import java.util.List;

import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerResponse;

/**
 * @author ganeshs
 *
 */
public class FilterChain {
	
	private List<Filter> filters;
	
	private Iterator<Filter> iterator;
	
	private RouteResolver resolver;
	
	private MessageContext context;
	
	public FilterChain(List<Filter> filters, RouteResolver resolver) {
		this.filters = filters;
		this.resolver = resolver;
	}
	
	public void doFilter(Request request, Response response) {
		if (iterator == null) {
			iterator = filters.iterator();
		}
		if (iterator.hasNext()) {
			iterator.next().doFilter(request, response, this);
		} else {
			Route route = resolver.resolve(context);
			Object result = route.getAction().invoke(request, response);
			if (result != null && ! ((ServerResponse) response).isContentSet()) {
				response.setContent(result);
			}
		}
	}
	
	void doFilter(MessageContext context) {
		this.context = context;
		doFilter(context.getRequest(), context.getResponse());
	}
}
