/**
 * 
 */
package org.minnal.instrument.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> A method marked with this annotation will automatically show up in the routes. We can make use of this annotation for all the domain operations
 * that we want to expose as a route.</p>
 * 
 * <p> This annotation is applicable only for domain models annotated with {@link AggregateRoot}. Minnal enforces the users to follow string domain modeling.
 * Any operations involving the children of the aggregate root should be driven by the root. For instance if you want to cancel 5 quantities of an order item, 
 * you should call cancel(orderItem, 5) on order which inturn would call orderItem. This way, any domain check (like can the order item be cancelled in the current state of order etc.. ) 
 * can be done at order level</p>
 * 
 * @author ganeshs
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

	/**
	 * The name of the action. A route will be created with this name converted to an underscore format
	 * 
	 * @return
	 */
	String value();
	
	/**
	 * A path to a child node in this aggregate separated by DOT. Ex: In order aggregate root, orderItems.shipments will denote the route "/orders/{order_id}/order_items/{order_item_id}/shipments/{shipment_id}"
	 * 
	 * @return
	 */
	String path() default "";
}
