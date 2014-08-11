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
import org.minnal.security.SecurityPlugin;
import org.pac4j.http.client.BasicAuthClient;
import org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

/**
 * @author ganeshs
 *
 */
public class OrderApplication extends Application<OrderConfiguration> {
	
	public OrderApplication() {
		ConvertUtils.register(EnumConverter.instance, Order.Status.class);
		ConvertUtils.register(EnumConverter.instance, OrderItem.Status.class);
	}

	protected void defineResources() {
	}
	
	@Override
	protected void registerPlugins() {
		registerPlugin(new JPAPlugin());
		// Add security plugin if enabled
		if(getConfiguration().isEnableSecurity()) {
			BasicAuthClient client = new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator());
			client.setName("basic");
			registerPlugin(new SecurityPlugin("/callback", client));
		}
	}
	
	@Override
	public void init() {
		super.init();
		Hibernate4Module module = new Hibernate4Module();
		module.configure(Feature.FORCE_LAZY_LOADING, true);
		getObjectMapper().registerModule(module);
	}
}
