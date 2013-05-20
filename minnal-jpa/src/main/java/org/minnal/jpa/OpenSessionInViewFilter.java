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

/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilter implements Filter {

	public void doFilter(Request request, Response response, FilterChain chain) {
		JPAContext context = JPA.instance.getDefaultConfig().getContext();
		context.beginTxn();
		try {
			chain.doFilter(request, response);
		} finally {
			if (context.isTxnOpen()) {
				context.closeTxn(true);
			}
			context.close();
		}
	}

}
