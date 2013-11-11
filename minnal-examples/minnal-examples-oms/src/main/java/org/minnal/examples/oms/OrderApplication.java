/**
 * 
 */
package org.minnal.examples.oms;

import org.activejpa.util.EnumConverter;
import org.apache.commons.beanutils.ConvertUtils;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.server.exception.InternalServerErrorException;
import org.minnal.examples.oms.auth.BasicAuthenticator;
import org.minnal.examples.oms.domain.Order;
import org.minnal.examples.oms.domain.OrderItem;
import org.minnal.examples.oms.resource.OrderResource;
import org.minnal.jpa.JPAPlugin;
import org.minnal.jpa.OpenSessionInViewFilter;
import org.minnal.security.filter.basic.BasicAuthenticationFilter;

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
	protected void defineRoutes() {
		resource(OrderResource.class).builder("/hello").action(HttpMethod.GET, "helloWorld");
	}

	@Override
	protected void defineResources() {
	}
	
	@Override
	protected void addFilters() {
		addFilter(new OpenSessionInViewFilter(getConfiguration().getDatabaseConfiguration()));
		if (getConfiguration().getSecurityConfiguration() != null) {
			addFilter(new BasicAuthenticationFilter(new BasicAuthenticator(), getConfiguration().getSecurityConfiguration()));
		}
	}
	
	@Override
	protected void registerPlugins() {
		registerPlugin(new JPAPlugin());
	}
	
	@Override
	protected void mapExceptions() {
		addExceptionMapping(MinnalException.class, InternalServerErrorException.class);
		super.mapExceptions();
	}
}
