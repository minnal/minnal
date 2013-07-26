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
import org.minnal.api.util.PropertyUtil;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.QueryParam;
import org.minnal.core.route.Route;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.RoutePattern.RouteElement;
import org.minnal.core.util.HttpUtil;

import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import com.wordnik.swagger.core.DocumentationSchema;

/**
 * @author ganeshs
 *
 */
public class ApiDocumentation {

	public static final ApiDocumentation instance = new ApiDocumentation();
	
	private Map<String, Map<String, Documentation>> resourceDocs = new HashMap<String, Map<String, Documentation>>();
	
	private Map<String, Documentation> applicationResources = new HashMap<String, Documentation>();
	
	private String baseUrl;
	
	private ApiDocumentation() {}
	
	public void addApplication(Application<ApplicationConfiguration> application) {
		buildResources(application);
		buildApis(application);
	}
	
	public Documentation getDocumentation(String applicationName, String resourceName) {
		Map<String, Documentation> docs = resourceDocs.get(applicationName);
		if (docs == null || docs.isEmpty()) {
			return null;
		}
		return docs.get(resourceName);
	}
	
	public Documentation getDocumentation(String applicationName) {
		return applicationResources.get(applicationName);
	}
	
	protected void buildApis(Application<ApplicationConfiguration> application) {
		Map<String, Documentation> docs = new HashMap<String, Documentation>();
		resourceDocs.put(application.getConfiguration().getName(), docs);
		for (ResourceClass resourceClass : application.getResources()) {
			docs.put(resourceClass.getConfiguration().getName(), createDocumentation(application, resourceClass));
		}
	}
	
	protected Documentation createDocumentation(Application<ApplicationConfiguration> application, ResourceClass resourceClass) {
		Documentation documentation = new Documentation();
		documentation.setSwaggerVersion("1.1");
		documentation.setBasePath(constructBasePath(application.getPath()));
		documentation.setResourcePath(resourceClass.getBasePath());
		documentation.setApis(createEndPoints(resourceClass.getRouteBuilders()));
		documentation.setModels(createSchemas(resourceClass));
		return documentation;
	}
	
	protected HashMap<String, DocumentationSchema> createSchemas(ResourceClass resourceClass) {
		HashMap<String, DocumentationSchema> schemas = new HashMap<String, DocumentationSchema>();
		Class<?> entityClass = resourceClass.getEntityClass();
		if (entityClass == null) {
			return schemas;
		}
		ApiDocumentationNode node = new ApiDocumentationNode(entityClass);
		node.construct();
		schemas = (HashMap<String, DocumentationSchema>) node.getModels();
		return schemas;
	}
	
	protected List<DocumentationEndPoint> createEndPoints(List<RouteBuilder> builders) {
		List<DocumentationEndPoint> eps = new ArrayList<DocumentationEndPoint>();
		for (RouteBuilder builder : builders) {
			DocumentationEndPoint ep = new DocumentationEndPoint();
			List<Route> routes = builder.build();
			ep.setPath(routes.get(0).getRoutePattern().getPathPattern());
			ep.setOperations(createOperations(routes));
			eps.add(ep);
		}
		return eps;
	}
	
	protected List<DocumentationOperation> createOperations(List<Route> routes) {
		List<DocumentationOperation> ops = new ArrayList<DocumentationOperation>();
		for (Route route : routes) {
			if (! isResource(route)) {
				continue;
			}
			if (route.getMethod().equals(HttpMethod.OPTIONS)) {
				continue;
			}
			DocumentationOperation op = new DocumentationOperation();
			op.setHttpMethod(route.getMethod().getName());
			op.setNickname(route.getAction().getMethod().getName());
			op.setParameters(createParameter(route));
			
			if (route.getMethod().equals(HttpMethod.GET)) {
				op.setResponseClass(getResponseClass(route));
			} else if (route.getMethod().equals(HttpMethod.POST)) {
				op.setResponseClass(getResponseClass(route));
			}
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
	
	protected List<DocumentationParameter> createParameter(Route route) {
		List<DocumentationParameter> parameters = new ArrayList<DocumentationParameter>();
		for (String name : route.getRoutePattern().getParameterNames()) {
			DocumentationParameter parameter = new DocumentationParameter();
			parameter.setParamType("path");
			parameter.setDataType("string");
			parameter.setName(name);
			parameter.setDescription("The " + name);
			parameter.setRequired(true);
			parameters.add(parameter);
		}
		
		for (QueryParam param : route.getQueryParams()) {
			DocumentationParameter parameter = new DocumentationParameter();
			parameter.setParamType("parameter");
			parameter.setDataType(param.getType().toString());
			parameter.setName(param.getName());
			parameter.setDescription(param.getDescription());
			parameters.add(parameter);
		}
		
		if (route.getMethod().equals(HttpMethod.POST) || route.getMethod().equals(HttpMethod.PUT)) {
			DocumentationParameter parameter = new DocumentationParameter();
			parameter.setParamType("body");
			parameter.setDataType(getRequestClass(route));
			parameters.add(parameter);
		}
		return parameters;
	}
	
	protected void buildResources(Application<ApplicationConfiguration> application) {
		Documentation documentation = new Documentation();
		documentation.setSwaggerVersion("1.1");
		for (ResourceClass resourceClass : application.getResources()) {
			DocumentationEndPoint ep = new DocumentationEndPoint();
			ep.setPath(HttpUtil.structureUrl(resourceClass.getConfiguration().getName()));
			documentation.addApi(ep);
		}
		applicationResources.put(application.getConfiguration().getName(), documentation);
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
	
	protected String constructBasePath(String path) {
		return baseUrl + path;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
