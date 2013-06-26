/**
 * 
 */
package org.minnal.api;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.api.resources.ApiResource;
import org.minnal.core.Application;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.serializer.DefaultJsonSerializer;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ApiApplication extends Application<ApiConfiguration> {

	@Override
	protected void registerPlugins() {
	}

	@Override
	protected void addFilters() {
	}

	@Override
	protected void defineRoutes() {
		resource(ApiResource.class).builder("/{application_name}/api-docs.json").action(HttpMethod.GET, "listResources");
		resource(ApiResource.class).builder("/{application_name}/{resource_name}").action(HttpMethod.GET, "listResourceApis");
	}

	@Override
	protected void defineResources() {
		ResourceConfiguration configuration = new ResourceConfiguration("api");
		configuration.addSerializer(MediaType.JSON_UTF_8, new DefaultJsonSerializer() {
			@Override
			public PropertyNamingStrategy getPropertyNamingStrategy() {
				return null;
			}
		});
		addResource(ApiResource.class, configuration);
	}

}
