/**
 * 
 */
package org.minnal.api;

import org.minnal.api.resources.ApiResource;
import org.minnal.core.Application;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.serializer.DefaultJsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
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
		resource(ApiResource.class).builder("/{application_name}/api-docs").action(HttpMethod.GET, "listResources");
		resource(ApiResource.class).builder("/{application_name}/api-docs/{resource_name}").action(HttpMethod.GET, "listResourceApis");
	}

	@Override
	protected void defineResources() {
		ResourceConfiguration configuration = new ResourceConfiguration("api");
		configuration.addSerializer(MediaType.JSON_UTF_8, new DefaultJsonSerializer() {
			@Override
			public PropertyNamingStrategy getPropertyNamingStrategy() {
				return null;
			}
			
			@Override
			protected void registerModules(ObjectMapper mapper) {
				mapper.registerModule(new DefaultScalaModule());
			}
		});
		addResource(ApiResource.class, configuration);
	}

}
