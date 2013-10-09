/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.javalite.common.Inflector;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.resource.Resource;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.QueryParam;
import org.minnal.core.route.RouteAction;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.RoutePattern;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * @author ganeshs
 *
 */
public class ResourceWrapper {
	
	private static Template createMethodTemplate;
	
	private static Template readMethodTemplate;
	
	private static Template deleteMethodTemplate;
	
	private static Template updateMethodTemplate;
	
	private static Template listMethodTemplate;
	
	private static Template actionMethodTemplate;
	
	static {
		Properties properties = new Properties();
		properties.put("runtime.log.logsystem.class", "org.minnal.core.util.Slf4jLogChute");
		VelocityEngine ve = new VelocityEngine(properties);
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		readMethodTemplate = ve.getTemplate("META-INF/templates/read_method.vm");
		createMethodTemplate = ve.getTemplate("META-INF/templates/create_method.vm");
		updateMethodTemplate = ve.getTemplate("META-INF/templates/update_method.vm");
		deleteMethodTemplate = ve.getTemplate("META-INF/templates/delete_method.vm");
		listMethodTemplate = ve.getTemplate("META-INF/templates/list_method.vm");
		actionMethodTemplate = ve.getTemplate("META-INF/templates/action_method.vm");
	}
	
	private CtClass generatedClass;
	
	private Map<ResourcePath, Map<HttpMethod, List<String>>> paths = new HashMap<ResourcePath, Map<HttpMethod, List<String>>>();
	
	private ClassPool classPool = ClassPool.getDefault();
	
	private ResourceClass resourceClass;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceWrapper.class);
	
	/**
	 * @param resourceClass
	 */
	public ResourceWrapper(ResourceClass resourceClass) {
		this.resourceClass = resourceClass;
		generatedClass = createGeneratedClass();
	}
	
	protected CtClass createGeneratedClass() {
		CtClass generatedClass = null;
		if (resourceClass.getResourceClass() != null) {
			logger.debug("Creating the generated class for the resource {}", resourceClass.getResourceClass());
			
			try {
				CtClass superClass = classPool.get(resourceClass.getResourceClass().getName());
				generatedClass = classPool.makeClass(resourceClass.getResourceClass().getName() + "Wrapper", superClass);
			} catch (Exception e) {
				logger.error("Failed while creating the generated class for the resource - " + resourceClass.getResourceClass(), e);
				throw new MinnalException("Failed while creating the generated class");
			}
		} else {
			if (resourceClass.getEntityClass() == null) {
				logger.error("Entity Class not defined in the resource class");
				throw new MinnalException("Entity Class not defined in the resource class");
			}
			
			logger.debug("Creating the generated class for the entity {}", resourceClass.getEntityClass());
			generatedClass = classPool.makeClass(resourceClass.getEntityClass().getName() + "Resource");
		}
		ConstPool constPool = generatedClass.getClassFile().getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation resourceAnnotation = new Annotation(Resource.class.getCanonicalName(), constPool);
		resourceAnnotation.addMemberValue("value", new ClassMemberValue(resourceClass.getEntityClass().getCanonicalName(), constPool));
		attr.setAnnotation(resourceAnnotation);
		generatedClass.getClassFile().addAttribute(attr);
		return generatedClass;
	}
	
	public void addPath(EntityNodePath path) {
		logger.debug("Adding the path {}", path);
		try {
			if (path.isReadAllowed()) { 
				addCrudMethod(new ResourcePath(path, true), HttpMethod.GET);
				addCrudMethod(new ResourcePath(path, false), HttpMethod.GET);
			}
			if (path.isCreateAllowed()) {
				addCrudMethod(new ResourcePath(path, true), HttpMethod.POST);
			}
			if (path.isUpdateAllowed()) {
				addCrudMethod(new ResourcePath(path, false), HttpMethod.PUT);
				addActionMethods(new ResourcePath(path, false), HttpMethod.PUT);
			}
			if (path.isDeleteAllowed()) {
				addCrudMethod(new ResourcePath(path, false), HttpMethod.DELETE);
			}
		} catch (Exception e) {
			logger.error("Failed while adding the path", e);
			throw new MinnalException(e);
		}
	}
	
	protected Template getMethodTemplate(ResourcePath resourcePath, HttpMethod method) {
		logger.debug("Getting the method template for the resource path {} and method {}", resourcePath, method);
		if (resourcePath.isAction()) {
			return actionMethodTemplate;
		} else if (resourcePath.isBulk()) {
			if (method.equals(HttpMethod.GET)) {
				return listMethodTemplate;
			}
			if (method.equals(HttpMethod.POST)) {
				return createMethodTemplate;
			}
		} else {
			if (method.equals(HttpMethod.GET)) {
				return readMethodTemplate;
			}
			if (method.equals(HttpMethod.PUT)) {
				return updateMethodTemplate;
			}
			if (method.equals(HttpMethod.DELETE)) {
				return deleteMethodTemplate;
			}
		}
		return null;
	}
	
	protected void addActionMethods(ResourcePath resourcePath, HttpMethod method) throws Exception {
		logger.debug("Adding the action methods for the resource path {} and method {}", resourcePath, method);
		if (resourcePath.getNodePath().size() > 1) {
			logger.debug("Not adding the action methods for the non root paths. Resource path - {} and method - {}", resourcePath, method);
			// Actions will be added only for the root paths
			return;
		}
		Set<ActionMetaData> actions = resourcePath.getNodePath().get(0).getEntityMetaData().getActionMethods();
		ResourcePath actionPath = null;
		for (ActionMetaData action : actions) {
			EntityNode node = resourcePath.getNodePath().get(0);
			EntityNodePath path = node.getEntityNodePath(action.getPath());
			actionPath = new ResourcePath(path, action.getName());
			if (! shouldCreateMethod(actionPath, method, action)) {
				logger.debug("Method seem to exist for resource path {}, method {} and action {}", actionPath, method, action.getName());
				continue;
			}
			addActionMethod(actionPath, method, action);
		}
	}
	
	protected void addActionMethod(ResourcePath resourcePath, HttpMethod method, ActionMetaData action) throws Exception {
		logger.debug("Adding the action method {} for the resource path {} and method {}", action.getName(), resourcePath, method);
		VelocityContext context = new VelocityContext();
		context.put("action", action);
		addMethod(resourcePath, method, context);
	}
	
	protected void addMethod(ResourcePath resourcePath, HttpMethod method, VelocityContext context) throws Exception {
		Template template = getMethodTemplate(resourcePath, method);
		if (template == null) {
			logger.error("FATAL!! Template not found for the resource path {} and method {}", resourcePath, method);
			// TODO Can't get here. Handle if it still gets here
			return;
		}

		context.put("inflector", Inflector.class);
		context.put("generator", this);
		context.put("path", resourcePath.getNodePath());
		if (resourcePath.isBulk()) {
			context.put("param_names", new RoutePattern(resourcePath.getBulkPath()).getParameterNames());
		} else {
			context.put("param_names", new RoutePattern(resourcePath.getSinglePath()).getParameterNames());
		}
		
		logger.debug("Adding the method with context {} and template {} for the resource path {} and method {}", context, template.getName(), resourcePath, method);
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		
		logger.trace("Constructed method string {}", writer);
		String methodName = makeMethod(writer);
		addMethodToPath(resourcePath, method, methodName);
	}
	
	protected void addCrudMethod(ResourcePath resourcePath, HttpMethod method) throws Exception {
		logger.debug("Adding the crud method for the resource path {} and method {}", resourcePath, method);
		
		if (! shouldCreateMethod(resourcePath, method)) {
			logger.debug("Method seem to exist for resource path {}, method {}", resourcePath, method);
			return;
		}
		
		VelocityContext context = new VelocityContext();
		addMethod(resourcePath, method, context);
	}
	
	protected boolean methodExists(String methodName) {
		logger.debug("Checking if a method exists with the name {} under the class {}", methodName, generatedClass);
		try {
			CtMethod[] methods = generatedClass.getMethods();
			CtClass[] params = new CtClass[]{ClassPool.getDefault().get(Request.class.getName()), 
					ClassPool.getDefault().get(Response.class.getName())};
			for (CtMethod method : methods) {
				if (method.getName().equals(methodName) && Arrays.equals(method.getParameterTypes(), params)) {
					return true;
				}
			}
			return false;
		} catch (javassist.NotFoundException e) {
			logger.debug("Method not found with the name {} under the class {}", methodName, generatedClass);
			return false;
		}
	}
	
	protected String makeMethod(StringWriter writer) throws Exception {
		logger.trace("Adding the method {} to the class {}", writer, generatedClass);
		CtMethod ctMethod = CtMethod.make(writer.toString(), generatedClass);
		if (! methodExists(ctMethod.getName())) {
			generatedClass.addMethod(ctMethod);
		}
		return ctMethod.getName();
	}
	
	protected boolean shouldCreateMethod(ResourcePath rPath, HttpMethod method, ActionMetaData action) {
		String path = rPath.getActionPath();
		return shouldCreateMethod(path, method);
	}
	
	protected boolean shouldCreateMethod(ResourcePath path, HttpMethod method) {
		return shouldCreateMethod(path.isBulk() ? path.getBulkPath() : 
				path.getSinglePath(), method);
	}
	
	protected boolean shouldCreateMethod(String path, HttpMethod method) {
		if (resourceClass.getResourceClass() != null) {
			if (resourceClass.hasRoute(path, method)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Wraps the generated class to the resource class
	 */
	public void wrap() {
		logger.info("Wrapping the generated class {} to the resource class", generatedClass);
		try {
			Class<?> clazz = generatedClass.toClass();
			resourceClass.setResourceClass(clazz);
		} catch (Exception e) {
			logger.error("Failed while wrapping the generated class to the resource class", e);
			throw new MinnalException(e);
		}
		createRoutes();
	}
	
	/**
	 * Creates the routes for the generated class
	 */
	protected void createRoutes() {
		logger.debug("Creating the routes for the generated class {}", generatedClass);
		ResourcePath path = null;
		RouteBuilder builder = null;
		for (Entry<ResourcePath, Map<HttpMethod, List<String>>> entry : paths.entrySet()) {
			path = entry.getKey();
			
			if (path.isAction()) {
				builder = resourceClass.builder(constructRoutePath(path.getActionPath()));
			} else if (path.isBulk()) {
				builder = resourceClass.builder(constructRoutePath(path.getBulkPath()));
			} else {
				builder = resourceClass.builder(constructRoutePath(path.getSinglePath()));
			}
			
			for (Entry<HttpMethod, List<String>> method : entry.getValue().entrySet()) {
				Type requestType = getRequestType(method.getKey(), path);
				Type responseType = getResponseType(method.getKey(), path);
				for (String name : method.getValue()) {
					RouteAction action = builder.action(method.getKey(), name, requestType, responseType);
					if (method.getKey().equals(HttpMethod.GET) && path.bulk) {
						for (QueryParam param : entry.getKey().getNodePath().getQueryParams()) {
							action.queryParam(param);
						}
					}
				}
			}
		}
	}
	
	private Type getRequestType(HttpMethod method, ResourcePath path) {
		if (! method.equals(HttpMethod.POST) && ! method.equals(HttpMethod.PUT)) {
			return Void.class;
		}
		EntityNodePath nodePath = path.getNodePath();
		EntityNode node = nodePath.get(nodePath.size() - 1);
		return node.getEntityMetaData().getEntityClass();
	}
	
	private Type getResponseType(HttpMethod method, ResourcePath path) {
		if (method.equals(HttpMethod.DELETE) || method.equals(HttpMethod.PUT)) {
			return Void.class;
		}
		EntityNodePath nodePath = path.getNodePath();
		EntityNode node = nodePath.get(nodePath.size() - 1);
		Class<?> entityClass = node.getEntityMetaData().getEntityClass();
		if (method.equals(HttpMethod.GET) && path.bulk) {
			return listOf(TypeToken.of(entityClass)).getType();
		}
		return entityClass;
	}
	
	private static <E> TypeToken<List<E>> listOf(TypeToken<E> elementType) {
		return new TypeToken<List<E>>() {}.where(new TypeParameter<E>() {}, elementType);
	}
	
	private String constructRoutePath(String path) {
		return path.substring(resourceClass.getBasePath().length());
	}
	
	private boolean addMethodToPath(ResourcePath resourcePath, HttpMethod method, String action) {
		logger.debug("Adding the method {} with http method {} to the path {}", action, method, resourcePath);
		Map<HttpMethod, List<String>> methods = paths.get(resourcePath);
		if (methods == null) {
			methods = new LinkedHashMap<HttpMethod, List<String>>();
			paths.put(resourcePath, methods);
		}
		List<String> actions = methods.get(method);
		if (actions == null) {
			actions = new ArrayList<String>();
			methods.put(method, actions);
		}
		methods.get(method).add(action);
		return true;
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	public static class ResourcePath {
		
		private EntityNodePath nodePath;
		
		private boolean bulk;
		
		private String action;
		
		/**
		 * @param nodePath
		 * @param bulk
		 */
		public ResourcePath(EntityNodePath nodePath, boolean bulk) {
			this.nodePath = nodePath;
			this.bulk = bulk;
		}
		
		/**
		 * @param nodePath
		 * @param action
		 */
		public ResourcePath(EntityNodePath nodePath, String action) {
			this(nodePath, false);
			this.action = action;
		}
		
		/**
		 * @return the nodePath
		 */
		public EntityNodePath getNodePath() {
			return nodePath;
		}

		/**
		 * @return the bulk
		 */
		public boolean isBulk() {
			return bulk;
		}
		
		/**
		 * @return the action
		 */
		public boolean isAction() {
			return StringUtils.isNotBlank(action);
		}

		/**
		 * @return the action
		 */
		public String getActionPath() {
			return getSinglePath() + "/" + Inflector.underscore(action);
		}
		
		public String getSinglePath() {
			return nodePath.getSinglePath();
		}
		
		public String getBulkPath() {
			return nodePath.getBulkPath();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((action == null) ? 0 : action.hashCode());
			result = prime * result + (bulk ? 1231 : 1237);
			result = prime * result
					+ ((nodePath == null) ? 0 : nodePath.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ResourcePath other = (ResourcePath) obj;
			if (action == null) {
				if (other.action != null)
					return false;
			} else if (!action.equals(other.action))
				return false;
			if (bulk != other.bulk)
				return false;
			if (nodePath == null) {
				if (other.nodePath != null)
					return false;
			} else if (!nodePath.equals(other.nodePath))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ResourcePath [nodePath=" + nodePath + ", bulk=" + bulk
					+ ", action=" + action + "]";
		}
		
	}
}
