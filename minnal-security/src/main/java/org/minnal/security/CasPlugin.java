/**
 * 
 */
package org.minnal.security;

import java.net.URI;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.Application;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.util.HttpUtil;
import org.minnal.security.auth.cas.CasPgtIouResource;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.cas.CasFilter;
import org.minnal.security.filter.cas.CasProxyCallbackFilter;

/**
 * @author ganeshs
 *
 */
public class CasPlugin implements Plugin {
	
	private SecurityConfiguration configuration;
	
	public CasPlugin(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	public void init(Application<? extends ApplicationConfiguration> application) {
		ResourceClass resource = new ResourceClass(new ResourceConfiguration("cas", application.getConfiguration()), CasPgtIouResource.class);
		URI uri = HttpUtil.createURI(configuration.getCasConfiguration().getCasProxyCallbackUrl());
		resource.builder(uri.getPath()).action(HttpMethod.GET, "casProxyCallback");
		application.addResource(resource);
		application.addFilter(new CasProxyCallbackFilter(configuration));
		application.addFilter(new CasFilter(configuration));
	}

	public void destroy() {
	}
}
