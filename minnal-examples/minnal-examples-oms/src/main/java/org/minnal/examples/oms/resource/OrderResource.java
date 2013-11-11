/**
 * 
 */
package org.minnal.examples.oms.resource;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.resource.Resource;
import org.minnal.examples.oms.domain.Order;

/**
 * @author ganeshs
 * 
 */
@Resource(Order.class)
public class OrderResource {
	
	public void helloWorld(Request request, Response response) {
		response.setContent("Hello");
		response.setStatus(HttpResponseStatus.OK);
	}
}
