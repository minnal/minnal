/**
 * 
 */
package org.minnal.instrument.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
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
	
	private Map<ResourcePath, Map<HttpMethod, String>> paths = new HashMap<ResourcePath, Map<HttpMethod, String>>();
	
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
			addMethod(new ResourcePath(path, true), HttpMethod.GET);
			addMethod(new ResourcePath(path, true), HttpMethod.POST);
//			addMethod(new ResourcePath(path, false), HttpMethod.PUT);
			addMethod(new ResourcePath(path, false), HttpMethod.GET);
//			addMethod(new ResourcePath(path, false), HttpMethod.DELETE);
		} catch (Exception e) {
			throw new MinnalException(e);
		}
	}
	
	protected void addMethod(ResourcePath resourcePath, HttpMethod method) throws Exception {
		MethodCreator creator = MethodCreator.getMethodCreator(wrapperClass, resourcePath, method);
		if (creator == null) {
			// TODO Can't get here. Handle if it still gets here
			return;
		}
		
		if (resourceClass.hasRoute(resourcePath.isBulk() ? resourcePath.getNodePath().getBulkPath() : 
			resourcePath.getNodePath().getSinglePath(), HttpMethod.GET)) {
			return;
		}
		
		creator.create();
		addMethodToPath(resourcePath, method, creator.getMethodName());
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
		for (Entry<ResourcePath, Map<HttpMethod, String>> entry : paths.entrySet()) {
			path = entry.getKey();
			if (path.isBulk()) {
				builder = resourceClass.builder(path.getNodePath().getBulkPath());
			} else {
				builder = resourceClass.builder(path.getNodePath().getSinglePath());
			}
			
			for (Entry<HttpMethod, String> method : entry.getValue().entrySet()) {
				builder.action(method.getKey(), method.getValue());
			}
		}
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
	
	
	public class ResourcePath {
		
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
