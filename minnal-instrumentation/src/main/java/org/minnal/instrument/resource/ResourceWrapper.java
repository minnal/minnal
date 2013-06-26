/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

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
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.QueryParam;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.RoutePattern;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

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
	}
	
	private CtClass generatedClass;
	
	private Map<ResourcePath, Map<HttpMethod, String>> paths = new HashMap<ResourcePath, Map<HttpMethod, String>>();
	
	private ClassPool classPool = ClassPool.getDefault();
	
	private ResourceClass resourceClass;
	
	public ResourceWrapper(ResourceClass resourceClass) {
		this.resourceClass = resourceClass;
		generatedClass = createGeneratedClass();
	}
	
	protected CtClass createGeneratedClass() {
		if (resourceClass.getResourceClass() != null) {
			try {
				CtClass superClass = classPool.get(resourceClass.getResourceClass().getName());
				return classPool.makeClass(resourceClass.getResourceClass().getName() + "Wrapper", superClass);
			} catch (Exception e) {
				throw new MinnalException("Failed while creating the generated class");
			}
		} else {
			if (resourceClass.getEntityClass() == null) {
				throw new MinnalException("Entity Class not defined in the resource class");
			}
			return classPool.makeClass(resourceClass.getEntityClass().getName() + "Resource");
		}
	}
	
	public void addPath(EntityNodePath path) {
		try {
			addMethod(new ResourcePath(path, true), HttpMethod.GET);
			addMethod(new ResourcePath(path, true), HttpMethod.POST);
			addMethod(new ResourcePath(path, false), HttpMethod.PUT);
			addMethod(new ResourcePath(path, false), HttpMethod.GET);
			addMethod(new ResourcePath(path, false), HttpMethod.DELETE);
		} catch (Exception e) {
			throw new MinnalException(e);
		}
	}
	
	protected Template getMethodTemplate(CtClass resourceWrapper, ResourcePath resourcePath, HttpMethod method) {
		if (resourcePath.isBulk()) {
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
	
	protected void addMethod(ResourcePath resourcePath, HttpMethod method) throws Exception {
		Template template = getMethodTemplate(generatedClass, resourcePath, method);
		if (template == null) {
			// TODO Can't get here. Handle if it still gets here
			return;
		}
		
		if (resourcePath.getNodePath().getBulkPath().equals("/facilities/{facility_id}/station_mappings")) {
			System.out.println();
		}
		
		if (! shouldCreateMethod(resourcePath, method)) {
			return;
		}
		
		VelocityContext context = new VelocityContext();
		context.put("inflector", Inflector.class);
		context.put("generator", this);
		context.put("path", resourcePath.getNodePath());
		if (resourcePath.isBulk()) {
			context.put("param_names", new RoutePattern(resourcePath.getNodePath().getBulkPath()).getParameterNames());
		} else {
			context.put("param_names", new RoutePattern(resourcePath.getNodePath().getSinglePath()).getParameterNames());
		}
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		String methodName = makeMethod(writer);
		addMethodToPath(resourcePath, method, methodName);
	}
	
	protected boolean methodExists(String methodName) {
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
			return false;
		}
	}
	
	protected String makeMethod(StringWriter writer) throws Exception {
		CtMethod ctMethod = CtMethod.make(writer.toString(), generatedClass);
		if (! methodExists(ctMethod.getName())) {
			generatedClass.addMethod(ctMethod);
		}
		return ctMethod.getName();
	}
	
	protected boolean shouldCreateMethod(ResourcePath path, HttpMethod method) {
		if (resourceClass.getResourceClass() != null) {
			if (resourceClass.hasRoute(path.isBulk() ? path.getNodePath().getBulkPath() : 
				path.getNodePath().getSinglePath(), method)) {
				return false;
			}
		}
		return true;
	}
	
	public void wrap() {
		try {
			Class<?> clazz = generatedClass.toClass();
			resourceClass.setResourceClass(clazz);
		} catch (Exception e) {
			throw new MinnalException(e);
		}
		createRoutes();
	}
	
	protected void createRoutes() {
		ResourcePath path = null;
		RouteBuilder builder = null;
		for (Entry<ResourcePath, Map<HttpMethod, String>> entry : paths.entrySet()) {
			path = entry.getKey();
			if (path.isBulk()) {
				builder = resourceClass.builder(constructRoutePath(path.getNodePath().getBulkPath()));
			} else {
				builder = resourceClass.builder(constructRoutePath(path.getNodePath().getSinglePath()));
			}
			
			for (Entry<HttpMethod, String> method : entry.getValue().entrySet()) {
				builder.action(method.getKey(), method.getValue());
			}
			
			for (QueryParam param : entry.getKey().getNodePath().getQueryParams()) {
				builder.queryParam(param);
			}
		}
	}
	
	private String constructRoutePath(String path) {
		return path.substring(resourceClass.getBasePath().length());
	}
	
	private boolean addMethodToPath(ResourcePath resourcePath, HttpMethod method, String action) {
		Map<HttpMethod, String> methods = paths.get(resourcePath);
		if (methods == null) {
			methods = new HashMap<HttpMethod, String>();
			paths.put(resourcePath, methods);
		}
		methods.put(method, action);
		return true;
	}
	
	public static class ResourcePath {
		
		private EntityNodePath nodePath;
		
		private boolean bulk;
		
		public ResourcePath(EntityNodePath nodePath, boolean bulk) {
			this.nodePath = nodePath;
			this.bulk = bulk;
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
			if (bulk != other.bulk)
				return false;
			if (nodePath == null) {
				if (other.nodePath != null)
					return false;
			} else if (!nodePath.equals(other.nodePath))
				return false;
			return true;
		}
	}
}
