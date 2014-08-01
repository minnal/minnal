/**
 * 
 */
package org.minnal.examples.oms.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


/**
 * @author ganeshs
 * 
 */
@Path("/orders")
public class OrderResource {
	
	@GET
	public Response getOrders() {
		return Response.ok("test").build();
	}
}
