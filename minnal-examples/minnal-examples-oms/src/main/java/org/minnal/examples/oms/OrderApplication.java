/**
 * 
 */
package org.minnal.examples.oms;

import org.activejpa.util.EnumConverter;
import org.apache.commons.beanutils.ConvertUtils;
import org.minnal.core.Application;
import org.minnal.examples.oms.domain.Order;
import org.minnal.examples.oms.domain.OrderItem;
import org.minnal.jpa.JPAPlugin;
import org.minnal.jpa.OpenSessionInViewFilter;

/**
 * @author ganeshs
 *
 */
public class OrderApplication extends Application<OrderConfiguration> {
	
	public OrderApplication() {
		ConvertUtils.register(EnumConverter.instance, Order.Status.class);
		ConvertUtils.register(EnumConverter.instance, OrderItem.Status.class);
	}


	@Override
	protected void defineResources() {
//		addResource(OrderResource.class);
	}
	
	@Override
	protected void addFilters() {
		addFilter(new OpenSessionInViewFilter(getConfiguration().getDatabaseConfiguration()));
//		if (getConfiguration().getSecurityConfiguration() != null) {
//			addFilter(new BasicAuthenticationFilter(new BasicAuthenticator(), getConfiguration().getSecurityConfiguration()));
//		}
	}
	
	@Override
	protected void registerPlugins() {
		registerPlugin(new JPAPlugin());
	}
	
	@Override
	protected void mapExceptions() {
		super.mapExceptions();
	}
}
