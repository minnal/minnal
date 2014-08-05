/**
 * 
 */
package org.minnal.examples.oms.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.wordnik.swagger.annotations.Api;


/**
 * @author ganeshs
 * 
 */
@Path("/orders")
@Api("/orders")
public class OrderResource {
	
	@GET
	public Response getOrders() {
		return Response.ok("test").build();
	}
}
