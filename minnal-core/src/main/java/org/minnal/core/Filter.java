/**
 * 
 */
package org.minnal.core;


/**
 * @author ganeshs
 *
 */
public interface Filter {

	void doFilter(Request request, Response response, FilterChain chain);
}
