/**
 * 
 */
package org.minnal.core;

import java.util.Iterator;
import java.util.List;

import org.minnal.core.route.Route;

/**
 * @author ganeshs
 *
 */
public class FilterChain {
	
	private List<Filter> filters;
	
	private Iterator<Filter> iterator;
	
	private Route route;
	
	public FilterChain(List<Filter> filters, Route route) {
		this.filters = filters;
		this.route = route;
	}
	
	public void doFilter(Request request, Response response) {
		if (iterator == null) {
			iterator = filters.iterator();
		}
		if (iterator.hasNext()) {
			iterator.next().doFilter(request, response, this);
		} else {
//			action.invoke(request, response);
		}
	}
}
