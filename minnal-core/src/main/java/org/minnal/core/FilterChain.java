/**
 * 
 */
package org.minnal.core;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerResponse;

import com.google.common.base.Joiner;

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
			Object result = "";
			if (context.getRequest().getHttpMethod().equals(HttpMethod.OPTIONS)) {
				handleOptionsRequest(request, response);
			} else {
				result = route.getAction().invoke(request, response);
			}
			if (result != null && ! ((ServerResponse) response).isContentSet()) {
				response.setContent(result);
			}
		}
	}
	
	protected void handleOptionsRequest(Request request, Response response) {
		Set<HttpMethod> allowedMethods = context.getApplication().getRoutes(context.getResourceClass()).getAllowedMethods(request);
		response.addHeader(HttpHeaders.Names.ALLOW, Joiner.on(", ").join(allowedMethods));
	}
	
	void doFilter(MessageContext context) {
		this.context = context;
		doFilter(context.getRequest(), context.getResponse());
	}
}
