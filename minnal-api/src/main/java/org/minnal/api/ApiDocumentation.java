/**
 * 
 */
package org.minnal.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.QueryParam;
import org.minnal.core.route.Route;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.RoutePattern.RouteElement;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.reflection.PropertyUtil;

import scala.Option;
import scala.Predef;
import scala.Some;
import scala.Tuple2;
import scala.collection.JavaConversions;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.wordnik.swagger.model.ApiDescription;
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
		scala.collection.immutable.List<String> mediaTypes = JavaConversions.asScalaIterable(
				Iterables.transform(resourceClass.getConfiguration().getSupportedMediaTypes(), Functions.toStringFunction())).toList();
		
		List<ApiDescription> apis = createApiDescriptions(resourceClass.getRouteBuilders());
		Map<String, Model> models = createModels(resourceClass);
		ApiListing apiListing = new ApiListing("1.0", SWAGGER_VERSION, baseUrl, 
				HttpUtil.structureUrl(resourceClass.getBasePath()), mediaTypes, mediaTypes, 
				null, null, JavaConversions.asScalaBuffer(apis).toList(), 
				Option.apply(JavaConversions.mapAsScalaMap(models).toMap(Predef.<Tuple2<String, Model>>conforms())), Option.apply(""), 1);
		return apiListing;
	}
	
	protected Map<String, Model> createModels(ResourceClass resourceClass) {
		Map<String, Model> schemas = new HashMap<String, Model>();
		Class<?> entityClass = resourceClass.getEntityClass();
		if (entityClass == null) {
			return schemas;
		}
		ApiDocumentationNode node = new ApiDocumentationNode(entityClass);
		node.construct();
		schemas = Maps.transformValues(node.getModels(), new Function<org.minnal.api.Model, Model>() {
			@Override
			public Model apply(org.minnal.api.Model input) {
				return input.toModel();
			}
		});
		return schemas;
	}
	
	protected List<ApiDescription> createApiDescriptions(List<RouteBuilder> builders) {
		List<ApiDescription> eps = new ArrayList<ApiDescription>();
		for (RouteBuilder builder : builders) {
			List<Route> routes = builder.build();
			List<Operation> operations = createOperations(routes);
			ApiDescription ep = new ApiDescription(routes.get(0).getRoutePattern().getPathPattern(), Option.apply(""), 
					JavaConversions.asScalaBuffer(operations).toList());
			eps.add(ep);
		}
		return eps;
	}
	
	protected List<Operation> createOperations(List<Route> routes) {
		List<Operation> ops = new ArrayList<Operation>();
		int i = 0;
		for (Route route : routes) {
			if (! isResource(route)) {
				continue;
			}
			if (route.getMethod().equals(HttpMethod.OPTIONS)) {
				continue;
			}
			scala.collection.immutable.List<String> mediaTypes = JavaConversions.asScalaIterable(
					Iterables.transform(route.getConfiguration().getSupportedMediaTypes(), Functions.toStringFunction())).toList();
			String responseClass = "";
			if (route.getMethod().equals(HttpMethod.GET) || route.getMethod().equals(HttpMethod.POST)) {
				responseClass = getResponseClass(route);
			}
			List<Parameter> parameters = createParameter(route);
			Operation op = new Operation(route.getMethod().getName(), "", "", responseClass, 
					route.getAction().getMethod().getName(), i++, mediaTypes, mediaTypes, null, null, 
					JavaConversions.asScalaBuffer(parameters).toList(), null, Option.apply(""));
			ops.add(op);
		}
		return ops;
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
	
	protected List<Parameter> createParameter(Route route) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		for (String name : route.getRoutePattern().getParameterNames()) {
			Parameter parameter = new Parameter(name, Option.apply("The " + name), Option.apply(""), true, false, "string", null, "path", null);
			parameters.add(parameter);
		}
		
		for (QueryParam param : route.getQueryParams()) {
			Parameter parameter = new Parameter(param.getName(), Option.apply(param.getDescription()), Option.apply(""), true, false, param.getType().toString(), null, "parameter", null);
			parameters.add(parameter);
		}
		
		if (route.getMethod().equals(HttpMethod.POST) || route.getMethod().equals(HttpMethod.PUT)) {
			Parameter parameter = new Parameter("", Option.apply(""), Option.apply(""), true, false, getRequestClass(route), null, "body", null);
			parameters.add(parameter);
		}
		return parameters;
	}
	
	protected void buildResources(Application<ApplicationConfiguration> application) {
		int i = 0;
		List<ApiListingReference> apis = new ArrayList<ApiListingReference>();
		for (ResourceClass resourceClass : application.getResources()) {
			String path = HttpUtil.structureUrl(resourceClass.getConfiguration().getName());
			ApiListingReference api = new ApiListingReference(path, new Some<String>(resourceClass.getConfiguration().getName()), i++);
			apis.add(api);
		}
		ResourceListing resourceListing = new ResourceListing("1.0", SWAGGER_VERSION, JavaConversions.asScalaBuffer(apis).toList(), null, null);
		applicationResources.put(application.getConfiguration().getName(), resourceListing);
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
}
