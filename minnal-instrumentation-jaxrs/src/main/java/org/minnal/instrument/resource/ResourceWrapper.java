/**
 * 
 */
package org.minnal.instrument.resource;

import io.netty.handler.codec.http.HttpHeaders;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.javalite.common.Inflector;
import org.minnal.instrument.DefaultNamingStrategy;
import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.PermissionMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMethodMetaData;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.route.RoutePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

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
	
	private Map<ResourcePath, List<MethodMetaData>> paths = new HashMap<ResourcePath, List<MethodMetaData>>();
	
	private ClassPool classPool = ClassPool.getDefault();
	
	private ResourceMetaData resource;
	
	private Map<ResourceMethodMetaData, RoutePattern> routes = new HashMap<ResourceMethodMetaData, RoutePattern>();
	
	private Class<?> entityClass;
	
	private Class<?> handlerClass;
	
	private String path;
	
	private NamingStrategy namingStrategy = new DefaultNamingStrategy();
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceWrapper.class);
	
	/**
	 * @author ganeshs
	 *
	 */
	public enum HTTPMethod {
		get(HttpMethod.GET, GET.class), 
		post(HttpMethod.POST, POST.class), 
		put(HttpMethod.PUT, PUT.class), 
		delete(HttpMethod.DELETE, DELETE.class), 
		head(HttpMethod.HEAD, HEAD.class),
		options(HttpMethod.OPTIONS, OPTIONS.class);
		
		private String method;
		
		private Class<? extends java.lang.annotation.Annotation> annotation;

		/**
		 * @param method
		 * @param annotation
		 */
		private HTTPMethod(String method, Class<? extends java.lang.annotation.Annotation> annotation) {
			this.method = method;
			this.annotation = annotation;
		}

		/**
		 * @return the method
		 */
		public String getMethod() {
			return method;
		}

		/**
		 * @return the annotation
		 */
		public Class<? extends java.lang.annotation.Annotation> getAnnotation() {
			return annotation;
		}
	}
	
	/**
	 * @param resource
	 * @param entityClass
	 */
	public ResourceWrapper(ResourceMetaData resource, Class<?> entityClass) {
		this.resource = resource;
		this.entityClass = entityClass;
		this.path = resource != null ? resource.getPath() : namingStrategy.getResourceName(namingStrategy.getEntityName(entityClass));
		constructRoutes(resource);
		generatedClass = createGeneratedClass();
	}
	
	/**
	 * @param entityClass
	 */
	public ResourceWrapper(Class<?> entityClass) {
		this(null, entityClass);
	}
	
	protected CtClass createGeneratedClass() {
		CtClass generatedClass = null;
		if (resource != null) {
			logger.debug("Creating the generated class for the resource {}", resource.getResourceClass());
			
			try {
				CtClass superClass = classPool.get(resource.getResourceClass().getName());
				superClass.defrost();
				generatedClass = classPool.makeClass(resource.getResourceClass().getName() + "Wrapper", superClass);
			} catch (Exception e) {
				logger.error("Failed while creating the generated class for the resource - " + resource.getResourceClass(), e);
				throw new MinnalInstrumentationException("Failed while creating the generated class", e);
			}
		} else {
			if (entityClass == null) {
				logger.error("Entity Class not defined in the resource wrapper");
				throw new MinnalInstrumentationException("Entity Class not defined in the resource class");
			}
			
			logger.debug("Creating the generated class for the entity {}", entityClass);
			generatedClass = classPool.makeClass(entityClass.getName() + "Resource");
		}
		ConstPool constPool = generatedClass.getClassFile().getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		Annotation resourceAnnotation = new Annotation(Path.class.getCanonicalName(), constPool);
		resourceAnnotation.addMemberValue("value", new StringMemberValue(path, constPool));
		attr.setAnnotation(resourceAnnotation);
		generatedClass.getClassFile().addAttribute(attr);
		return generatedClass;
	}
	
	public void addPath(EntityNodePath path) {
		logger.debug("Adding the path {}", path);
		try {
			if (path.isReadAllowed()) { 
				addCrudMethod(new ResourcePath(path, true), HTTPMethod.get);
				addCrudMethod(new ResourcePath(path, false), HTTPMethod.get);
			}
			if (path.isCreateAllowed()) {
				addCrudMethod(new ResourcePath(path, true), HTTPMethod.post);
			}
			if (path.isUpdateAllowed()) {
				addCrudMethod(new ResourcePath(path, false), HTTPMethod.put);
				addActionMethods(new ResourcePath(path, false), HTTPMethod.put);
			}
			if (path.isDeleteAllowed()) {
				addCrudMethod(new ResourcePath(path, false), HTTPMethod.delete);
			}
		} catch (Exception e) {
			logger.error("Failed while adding the path", e);
			throw new MinnalInstrumentationException(e);
		}
	}
	
	protected Template getMethodTemplate(ResourcePath resourcePath, HTTPMethod httpMethod) {
		logger.debug("Getting the method template for the resource path {} and method {}", resourcePath, httpMethod);
		if (resourcePath.isAction()) {
			return actionMethodTemplate;
		} else if (resourcePath.isBulk()) {
			if (httpMethod.equals(HTTPMethod.get)) {
				return listMethodTemplate;
			}
			if (httpMethod.equals(HTTPMethod.post)) {
				return createMethodTemplate;
			}
		} else {
			if (httpMethod.equals(HTTPMethod.get)) {
				return readMethodTemplate;
			}
			if (httpMethod.equals(HTTPMethod.put)) {
				return updateMethodTemplate;
			}
			if (httpMethod.equals(HTTPMethod.delete)) {
				return deleteMethodTemplate;
			}
		}
		return null;
	}
	
	protected void addActionMethods(ResourcePath resourcePath, HTTPMethod httpMethod) throws Exception {
		logger.debug("Adding the action methods for the resource path {} and method {}", resourcePath, httpMethod);
		if (resourcePath.getNodePath().size() > 1) {
			logger.debug("Not adding the action methods for the non root paths. Resource path - {} and method - {}", resourcePath, httpMethod);
			// Actions will be added only for the root paths
			return;
		}
		Set<ActionMetaData> actions = resourcePath.getNodePath().get(0).getEntityMetaData().getActionMethods();
		ResourcePath actionPath = null;
		for (ActionMetaData action : actions) {
			EntityNode node = resourcePath.getNodePath().get(0);
			EntityNodePath path = node.getEntityNodePath(action.getPath());
			actionPath = new ResourcePath(path, action.getName());
			if (! shouldCreateMethod(actionPath, httpMethod, action)) {
				logger.debug("Method seem to exist for resource path {}, method {} and action {}", actionPath, httpMethod, action.getName());
				continue;
			}
			addActionMethod(actionPath, httpMethod, action);
		}
	}
	
	protected void addActionMethod(ResourcePath resourcePath, HTTPMethod httpMethod, ActionMetaData action) throws Exception {
		logger.debug("Adding the action method {} for the resource path {} and method {}", action.getName(), resourcePath, httpMethod);
		VelocityContext context = new VelocityContext();
		context.put("action", action);
		String methodName = addMethod(resourcePath, httpMethod, context);
		if (methodName != null) {
			addMethodToPath(resourcePath, httpMethod, methodName, action.getPermissionMetaData());
		}
	}
	
	protected String addMethod(ResourcePath resourcePath, HTTPMethod httpMethod, VelocityContext context) throws Exception {
		Template template = getMethodTemplate(resourcePath, httpMethod);
		if (template == null) {
			logger.error("FATAL!! Template not found for the resource path {} and method {}", resourcePath, httpMethod);
			// TODO Can't get here. Handle if it still gets here
			return null;
		}

		context.put("inflector", Inflector.class);
		context.put("generator", this);
		context.put("path", resourcePath.getNodePath());
		if (resourcePath.isBulk()) {
			context.put("param_names", new RoutePattern(resourcePath.getBulkPath()).getParameterNames());
		} else {
			context.put("param_names", new RoutePattern(resourcePath.getSinglePath()).getParameterNames());
		}
		
		logger.debug("Adding the method with context {} and template {} for the resource path {} and method {}", context, template.getName(), resourcePath, httpMethod);
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		
		logger.trace("Constructed method string {}", writer);
		return makeMethod(writer, httpMethod, resourcePath);
	}
	
	protected void addCrudMethod(ResourcePath resourcePath, HTTPMethod httpMethod) throws Exception {
		logger.debug("Adding the crud method for the resource path {} and method {}", resourcePath, httpMethod);
		
		if (! shouldCreateMethod(resourcePath, httpMethod)) {
			logger.debug("Method seem to exist for resource path {}, method {}", resourcePath, httpMethod);
			return;
		}
		
		VelocityContext context = new VelocityContext();
		String methodName = addMethod(resourcePath, httpMethod, context);
		if (methodName != null) {
			Set<PermissionMetaData> permissions = null;
			if (resourcePath.getNodePath().size() == 1) {
				permissions = resourcePath.getNodePath().get(0).getEntityMetaData().getPermissionMetaData();
			} else {
				CollectionMetaData source = resourcePath.getNodePath().get(resourcePath.getNodePath().size() - 1).getSource();
				if (source != null) {
					permissions = source.getPermissionMetaData();
				}
			}
			if (methodName != null) {
				addMethodToPath(resourcePath, httpMethod, methodName, permissions);
			}
		}
	}
	
	protected boolean methodExists(String methodName) {
		logger.debug("Checking if a method exists with the name {} under the class {}", methodName, generatedClass);
		try {
			CtMethod[] methods = generatedClass.getMethods();
			CtClass[] params = new CtClass[]{ClassPool.getDefault().get(HttpHeaders.class.getName()),
					ClassPool.getDefault().get(UriInfo.class.getName()),
					ClassPool.getDefault().get(Providers.class.getName()),
					ClassPool.getDefault().get(byte[].class.getName())};
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
	
	protected String makeMethod(StringWriter writer, HTTPMethod httpMethod, ResourcePath resourcePath) throws Exception {
		logger.trace("Adding the method {} to the class {}", writer, generatedClass);
		CtMethod ctMethod = CtMethod.make(writer.toString(), generatedClass);
		String path = ! Strings.isNullOrEmpty(resourcePath.action) ? resourcePath.getActionPath() : resourcePath.bulk ? resourcePath.getBulkPath() : resourcePath.getSinglePath();
		String relativePath = HttpUtil.deriveRelativePath(this.path, path);
		if (! Strings.isNullOrEmpty(relativePath)) {
			addAnnotationToMethod(ctMethod, Path.class, HttpUtil.deriveRelativePath(this.path, path));
		}
		addAnnotationToMethod(ctMethod, httpMethod.getAnnotation(), null);
		addAnnotationToMethodAttributes(ctMethod.getMethodInfo().getConstPool(), ctMethod.getMethodInfo(), Context.class, Context.class, Context.class, null);
		if (! methodExists(ctMethod.getName())) {
			generatedClass.addMethod(ctMethod);
		}
		return ctMethod.getName();
	}
	
	private void addAnnotationToMethod(CtMethod method, Class<? extends java.lang.annotation.Annotation> annotationClass, String value) {
		ConstPool constPool = generatedClass.getClassFile().getConstPool();
		MethodInfo methodInfo = method.getMethodInfo();
		
		AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
		if (attr == null) {
			attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			methodInfo.addAttribute(attr);
		}
		
		Annotation annotation = new Annotation(annotationClass.getCanonicalName(), constPool);
		if (value != null) {
			annotation.addMemberValue("value", new StringMemberValue(value, constPool));
		}
		attr.addAnnotation(annotation);
	}
	
	private void addAnnotationToMethodAttributes(ConstPool constPool, MethodInfo methodInfo, Class<? extends java.lang.annotation.Annotation>... annotationClasses) {
		if (annotationClasses == null) {
			return;
		}
		ParameterAnnotationsAttribute paramAtrributeInfo = (ParameterAnnotationsAttribute) methodInfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
		if (paramAtrributeInfo == null) {
			paramAtrributeInfo = new ParameterAnnotationsAttribute(constPool, ParameterAnnotationsAttribute.visibleTag);
			methodInfo.addAttribute(paramAtrributeInfo);
		}
		Annotation[][] annotations = new Annotation[annotationClasses.length][0];
		for (int i = 0; i < annotationClasses.length; i++) {
			if (annotationClasses[i] == null) {
				continue;
			}
			Annotation parameterAnnotation = new Annotation(annotationClasses[i].getCanonicalName(), constPool);
			annotations[i] = new Annotation[1];
			annotations[i][0] = parameterAnnotation;
		}
		paramAtrributeInfo.setAnnotations(annotations);
	}
	
	protected boolean shouldCreateMethod(ResourcePath rPath, HTTPMethod httpMethod, ActionMetaData action) {
		String path = rPath.getActionPath();
		return shouldCreateMethod(path, httpMethod);
	}
	
	protected boolean shouldCreateMethod(ResourcePath path, HTTPMethod httpMethod) {
		return shouldCreateMethod(path.isBulk() ? path.getBulkPath() : 
				path.getSinglePath(), httpMethod);
	}
	
	/**
	 * Checks if a method for the given path should be created in the generated class
	 * 
	 * @param path
	 * @param httpMethod
	 * @return
	 */
	protected boolean shouldCreateMethod(String path, HTTPMethod httpMethod) {
		return resource != null ? !hasRoute(path, httpMethod) : true;
	}
	
	/**
	 * Constructs the routes from the resource
	 * 
	 * @param resource
	 */
	protected void constructRoutes(ResourceMetaData resource) {
		if (resource != null) {
			for (ResourceMethodMetaData resourceMethod : resource.getAllResourceMethods()) {
				routes.put(resourceMethod, resourceMethod.getPattern());
			}
		}
	}
	
	/**
	 * Checks if a route with the given path and method exist in the resource
	 * 
	 * @param resource
	 * @param path
	 * @param method
	 * @return
	 */
	protected boolean hasRoute(String path, HTTPMethod httpMethod) {
		RoutePattern pattern = new RoutePattern(path);
		for (Entry<ResourceMethodMetaData, RoutePattern> entry : routes.entrySet()) {
			if (entry.getValue().equals(pattern) && entry.getKey().getHttpMethod().equalsIgnoreCase(httpMethod.getMethod())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Wraps the generated class to the resource class
	 */
	public Class<?> wrap() {
		logger.info("Wrapping the generated class {} to the resource class", generatedClass);
		try {
			handlerClass = generatedClass.toClass();
		} catch (Exception e) {
			logger.error("Failed while wrapping the generated class to the resource class", e);
			throw new MinnalInstrumentationException(e);
		}
		return handlerClass;
	}
	
	/**
	 * @return
	 */
	protected Class<?> getHandlerClass() {
		return handlerClass;
	}

	private void addMethodToPath(ResourcePath resourcePath, HTTPMethod httpMethod, String action, Set<PermissionMetaData> permissions) {
		logger.debug("Adding the method {} with the http Method {} to the path {}", action, httpMethod, resourcePath);
		
		List<String> perms = new ArrayList<String>();
		for (PermissionMetaData metaData : permissions) {
			if (metaData.getMethod().equals(httpMethod)) {
				perms = metaData.getPermissions();
			}
		}
		MethodMetaData metaData = new MethodMetaData(action, httpMethod.getMethod(), perms);
		List<MethodMetaData> methods = paths.get(resourcePath);
		if (methods == null) {
			methods = new ArrayList<MethodMetaData>();
			paths.put(resourcePath, methods);
		}
		methods.add(metaData);
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
	
	public static class MethodMetaData {
		
		private String name;
		
		private String httpMethod;
		
		private List<String> permissions;

		/**
		 * @param name
		 * @param httpMethod
		 * @param permissions
		 */
		public MethodMetaData(String name, String httpMethod,
				List<String> permissions) {
			this.name = name;
			this.httpMethod = httpMethod;
			this.permissions = permissions;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the httpMethod
		 */
		public String getHttpMethod() {
			return httpMethod;
		}

		/**
		 * @param httpMethod the httpMethod to set
		 */
		public void setHttpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
		}

		/**
		 * @return the permissions
		 */
		public List<String> getPermissions() {
			return permissions;
		}

		/**
		 * @param permissions the permissions to set
		 */
		public void setPermissions(List<String> permissions) {
			this.permissions = permissions;
		}

		@Override
		public String toString() {
			return "MethodMetaData [name=" + name + ", httpMethod="
					+ httpMethod + ", permissions=" + permissions + "]";
		}
	}
}
