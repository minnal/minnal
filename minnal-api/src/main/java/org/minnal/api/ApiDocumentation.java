/**
 * 
 */
package org.minnal.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.Configuration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.QueryParam;
import org.minnal.core.route.Route;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.RoutePattern.RouteElement;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.reflection.PropertyUtil;

import scala.Option;
import scala.collection.JavaConversions;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ApiListingReference;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.Operation;
import com.wordnik.swagger.model.Parameter;
import com.wordnik.swagger.model.ResourceListing;

/**
 * @author ganeshs
 *
 */
public class ApiDocumentation {

	public static final ApiDocumentation instance = new ApiDocumentation();
	
	private Map<String, Map<String, ApiListing>> resourceDocs = new HashMap<String, Map<String, ApiListing>>();
	
	private Map<String, ResourceListing> applicationResources = new HashMap<String, ResourceListing>();
	
	private String baseUrl;
	
	public static final String SWAGGER_VERSION = "1.2";
	
	private ApiDocumentation() {}
	
	public void addApplication(Application<ApplicationConfiguration> application) {
		buildResources(application);
		buildApis(application);
	}
	
	public ApiListing getApiListing(String applicationName, String resourceName) {
		Map<String, ApiListing> docs = resourceDocs.get(applicationName);
		if (docs == null || docs.isEmpty()) {
			return null;
		}
		return docs.get(resourceName);
	}
	
	public ResourceListing getResourceListing(String applicationName) {
		return applicationResources.get(applicationName);
	}
	
	protected void buildApis(Application<ApplicationConfiguration> application) {
		Map<String, ApiListing> docs = new HashMap<String, ApiListing>();
		resourceDocs.put(application.getConfiguration().getName(), docs);
		for (ResourceClass resourceClass : application.getResources()) {
			docs.put(resourceClass.getConfiguration().getName(), createApiListing(application, resourceClass));
		}
	}
	
	protected ApiListing createApiListing(Application<ApplicationConfiguration> application, ResourceClass resourceClass) {
		String resourcePath = HttpUtil.structureUrl(resourceClass.getBasePath());
		scala.collection.immutable.List<String> produces = getMediaTypes(resourceClass.getConfiguration());
		scala.collection.immutable.List<ApiDescription> apiDescription = createApiDescription(resourceClass.getRouteBuilders());
		Option<scala.collection.immutable.Map<String, Model>> models = Option.apply(createSchemas(resourceClass));
		return new ApiListing(application.getConfiguration().getApiVersion(), SWAGGER_VERSION, baseUrl, resourcePath, produces, produces, null, null, 
				apiDescription, models, Option.apply(""), 0);
	}
	
	protected scala.collection.immutable.Map<String, Model> createSchemas(ResourceClass resourceClass) {
		HashMap<String, Model> schemas = new HashMap<String, Model>();
		Class<?> entityClass = resourceClass.getEntityClass();
		if (entityClass != null) {
			ApiDocumentationNode node = new ApiDocumentationNode(entityClass);
			node.construct();
			schemas = (HashMap<String, Model>) node.getModels();
		}
		return scala.collection.immutable.Map$.MODULE$.<String, Model>apply(JavaConversions.asScalaMap(schemas).toSeq());
	}
	
	protected scala.collection.immutable.List<ApiDescription> createApiDescription(List<RouteBuilder> builders) {
		List<ApiDescription> eps = new ArrayList<ApiDescription>();
		for (RouteBuilder builder : builders) {
			List<Route> routes = builder.build();
			Option<String> description = Option.apply(""); // TODO get the description from the route builder
			ApiDescription ep = new ApiDescription(routes.get(0).getRoutePattern().getPathPattern(), description, createOperations(routes));
			eps.add(ep);
		}
		Collections.sort(eps, new Comparator<ApiDescription>() {
			@Override
			public int compare(ApiDescription o1, ApiDescription o2) {
				return o1.path().compareTo(o2.path());
			}
		});
		return JavaConversions.asScalaBuffer(eps).toList();
	}
	
	protected scala.collection.immutable.List<Operation> createOperations(List<Route> routes) {
		List<Operation> ops = new ArrayList<Operation>();
		for (Route route : routes) {
			if (! isResource(route)) {
				continue;
			}
			if (route.getMethod().equals(HttpMethod.OPTIONS)) {
				continue;
			}
			
			String responseClass = null;
			if (route.getMethod().equals(HttpMethod.GET) || route.getMethod().equals(HttpMethod.POST)) {
				responseClass = getResponseClass(route);
			}
			
			scala.collection.immutable.List<String> produces = getMediaTypes(route.getConfiguration());
			Operation op = new Operation(route.getMethod().getName(), route.getDescription(), route.getNotes(), 
					responseClass, route.getAction().getMethod().getName(), 0, produces, produces, null, null, createParameter(route), null, Option.apply(""));
			ops.add(op);
		}
		return JavaConversions.asScalaBuffer(ops).toList();
	}
	
	private String getResponseClass(Route route) {
		return getType(route.getResponseType());
	}
	
	private String getRequestClass(Route route) {
		return getType(route.getRequestType());
	}
	
	private String getType(Type type) {
		if (type instanceof Class) {
			return ((Class<?>)type).getSimpleName();
		}
		
		if (type instanceof ParameterizedType) {
			if (PropertyUtil.isCollectionProperty(type, false)) {
				return "Array[" + PropertyUtil.getCollectionElementType(type).getSimpleName() + "]";
			} else if (PropertyUtil.isMapProperty(type)) {
				return "Object";
			}
		}
		return "Object";
	}
	
	protected scala.collection.immutable.List<Parameter> createParameter(Route route) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		for (String name : route.getRoutePattern().getParameterNames()) {
			Parameter parameter = new Parameter(name, Option.apply("The " + name), Option.apply(""), true, true, "string", null, "path", Option.apply(""));
			parameters.add(parameter);
		}
		
		for (QueryParam param : route.getQueryParams()) {
			Parameter parameter = new Parameter(param.getName(), Option.apply(param.getDescription()), Option.apply(""), true, false, 
					param.getType().toString(), null, "parameter", Option.apply(""));
			parameters.add(parameter);
		}
		
		if (route.getMethod().equals(HttpMethod.POST) || route.getMethod().equals(HttpMethod.PUT)) {
			Parameter parameter = new Parameter("body", Option.apply("Request Payload"), Option.apply(""), true, false, getRequestClass(route), null, "body", Option.apply(""));
			parameters.add(parameter);
		}
		return JavaConversions.asScalaBuffer(parameters).toList();
	}
	
	protected void buildResources(Application<ApplicationConfiguration> application) {
		List<ApiListingReference> apis = new ArrayList<ApiListingReference>();
		for (ResourceClass resourceClass : application.getResources()) {
			String resourceName = resourceClass.getConfiguration().getName();
			String description = "Operations about " + resourceName;
			ApiListingReference ep = new ApiListingReference(HttpUtil.structureUrl(resourceName), Option.apply(description), 0);
			apis.add(ep);
		}
		Collections.sort(apis, new Comparator<ApiListingReference>(){
			@Override
			public int compare(ApiListingReference o1, ApiListingReference o2) {
				return o1.path().compareTo(o2.path());
			}
		});
		scala.collection.immutable.List<ApiListingReference> apiList = JavaConversions.asScalaBuffer(apis).toList();
		ApiInfo apiInfo = new ApiInfo(application.getConfiguration().getName(), "", "", "", "", "");
		ResourceListing listing = new ResourceListing(application.getConfiguration().getApiVersion(), SWAGGER_VERSION, apiList, null, Option.apply(apiInfo));
		applicationResources.put(application.getConfiguration().getName(), listing);
	}
	
	protected boolean isResource(Route route) {
		List<RouteElement> elements = route.getRoutePattern().getElements();
		if (elements.isEmpty()) {
			return false;
		}
		RouteElement element = elements.get(0);
		return ! element.isParameter();
	}
	
	protected String getResourcePath(Route route) {
		return "/" + route.getRoutePattern().getElements().get(0).getName();
	}
	
	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Returns the supported media types from configuration in a scala list
	 *  
	 * @param configuration
	 * @return
	 */
	protected scala.collection.immutable.List<String> getMediaTypes(Configuration configuration) {
		Set<MediaType> mediaTypes = configuration.getSupportedMediaTypes();
		List<String> list = new ArrayList<String>();
		if (mediaTypes != null && ! mediaTypes.isEmpty()) {
			list = Lists.newArrayList(Iterators.transform(mediaTypes.iterator(), new Function<MediaType, String>() {
				@Override
				public String apply(MediaType input) {
					return input.toString();
				}
			}));
		}
		return JavaConversions.asScalaBuffer(list).toList();
	}
}
