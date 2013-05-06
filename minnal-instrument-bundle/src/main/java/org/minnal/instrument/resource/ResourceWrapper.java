/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.RouteBuilder;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

/**
 * @author ganeshs
 *
 */
public class ResourceWrapper {
	
	private ResourceClass resourceClass;
	
	private CtClass wrapperClass;
	
	private Map<ResourcePath, Set<HttpMethod>> paths = new HashMap<ResourcePath, Set<HttpMethod>>();
	
	private ClassPool classPool = ClassPool.getDefault();
	
	public ResourceWrapper(ResourceClass resourceClass) {
		this.resourceClass = resourceClass;
		try {
			CtClass superClass = classPool.get(resourceClass.getResourceClass().getName());
			wrapperClass = classPool.makeClass(resourceClass.getResourceClass().getName() + "Wrapper", superClass);
		} catch (NotFoundException e) {
			throw new MinnalException(e);
		}
	}
	
	public void addPath(EntityNodePath path) {
		try {
			addListMethod(path);
			addReadMethod(path);
			addCreateMethod(path);
			addUpdateMethod(path);
			addDeleteMethod(path);
		} catch (Exception e) {
			throw new MinnalException(e);
		}
	}
	
	public void wrap() {
		try {
			resourceClass.setResourceClass(wrapperClass.toClass());
		} catch (Exception e) {
			throw new MinnalException(e);
		}
		createRoutes();
	}
	
	protected void createRoutes() {
		ResourcePath path = null;
		RouteBuilder builder = null;
		for (Entry<ResourcePath, Set<HttpMethod>> entry : paths.entrySet()) {
			path = entry.getKey();
			if (path.bulk) {
				builder = resourceClass.builder(path.nodePath.getBulkPath());
			} else {
				builder = resourceClass.builder(path.nodePath.getSinglePath());
			}
			
			for (HttpMethod method : entry.getValue()) {
				builder.action(method, getMethodName(path.nodePath, path.bulk, method));
			}
		}
	}
	
	private String getMethodName(EntityNodePath path, boolean bulk, HttpMethod httpMethod) {
		if (bulk) {
			if (httpMethod.equals(HttpMethod.GET)) {
				return "list" + path.getName();
			} else if (httpMethod.equals(HttpMethod.POST)) {
				return "create" + path.getName();
			}
		} else {
			if (httpMethod.equals(HttpMethod.GET)) {
				return "get" + path.getName();
			} else if (httpMethod.equals(HttpMethod.PUT)) {
				return "update" + path.getName();
			} else if (httpMethod.equals(HttpMethod.DELETE)) {
				return "delete" + path.getName();
			}
		}
		// Shouldn't get here
		throw new IllegalArgumentException("Invalid http method - " + httpMethod + " for the path. Bulk - " + bulk);
	}
	
	private boolean methodExists(String methodName) {
		try {
			wrapperClass.getDeclaredMethod(methodName, new CtClass[]{classPool.get(Request.class.getName()), classPool.get(Request.class.getName())});
			return true;
		} catch (NotFoundException e) {
			return false;
		}
	}
	
	private boolean addMethodToPath(EntityNodePath path, boolean bulk, HttpMethod method) {
		if (resourceClass.hasRoute(bulk ? path.getBulkPath() : path.getSinglePath(), HttpMethod.GET)) {
			return false;
		}
		ResourcePath resourcePath = new ResourcePath(path, bulk);
		Set<HttpMethod> methods = paths.get(resourcePath);
		if (methods == null) {
			methods = new HashSet<HttpMethod>();
			paths.put(resourcePath, methods);
		}
		methods.add(method);
		return true;
	}
	
	protected void addListMethod(EntityNodePath path) throws Exception {
		createMethod(path, true, HttpMethod.GET, "");
	}
	
	protected void addReadMethod(EntityNodePath path) throws Exception {
		createMethod(path, false, HttpMethod.GET, "");
	}
	
	protected void addUpdateMethod(EntityNodePath path) throws Exception {
		createMethod(path, false, HttpMethod.PUT, "");
	}
	
	protected void addCreateMethod(EntityNodePath path) throws Exception {
		createMethod(path, true, HttpMethod.POST, "");
	}
	
	protected void addDeleteMethod(EntityNodePath path) throws Exception {
		createMethod(path, false, HttpMethod.DELETE, "");
	}
	
	private void createMethod(EntityNodePath path, boolean bulk, HttpMethod httpMethod, String body) throws Exception {
		if (! addMethodToPath(path, bulk, httpMethod) ) {
			return;
		}
		String methodName = getMethodName(path, bulk, httpMethod);
		if (methodExists(methodName)) {
			return;
		}
		if (wrapperClass.isFrozen()) {
			wrapperClass.defrost();
		}
		StringWriter writer = new StringWriter();
		writer.append("public void ").append(methodName).append("(").append(Request.class.getName()).append(" request, ");
		writer.append(Response.class.getName()).append(" response) {").append(body).append("}");
		CtMethod method = CtNewMethod.make(writer.toString(), wrapperClass);
		wrapperClass.addMethod(method);
	}
	
	private class ResourcePath {
		
		private EntityNodePath nodePath;
		
		private boolean bulk;

		public ResourcePath(EntityNodePath nodePath, boolean bulk) {
			this.nodePath = nodePath;
			this.bulk = bulk;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
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
			if (!getOuterType().equals(other.getOuterType()))
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

		private ResourceWrapper getOuterType() {
			return ResourceWrapper.this;
		}
		
	}
}
