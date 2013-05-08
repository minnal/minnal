/**
 * 
 */
package org.minnal.instrument.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
			if (path.isBulk()) {
				builder = resourceClass.builder(path.getNodePath().getBulkPath());
			} else {
				builder = resourceClass.builder(path.getNodePath().getSinglePath());
			}
			
			for (HttpMethod method : entry.getValue()) {
				builder.action(method, path.getAction());
			}
		}
	}
	
	private boolean addMethodToPath(EntityNodePath path, boolean bulk, HttpMethod method, String action) {
		if (resourceClass.hasRoute(bulk ? path.getBulkPath() : path.getSinglePath(), HttpMethod.GET)) {
			return false;
		}
		ResourcePath resourcePath = new ResourcePath(path, bulk, action);
		Set<HttpMethod> methods = paths.get(resourcePath);
		if (methods == null) {
			methods = new HashSet<HttpMethod>();
			paths.put(resourcePath, methods);
		}
		methods.add(method);
		return true;
	}
	
	protected void addListMethod(EntityNodePath path) throws Exception {
		ListMethodCreator creator = new ListMethodCreator(wrapperClass, path);
		if (! addMethodToPath(path, true, HttpMethod.GET, creator.getMethodName())) {
			return;
		}
		creator.create();
	}
	
	protected void addReadMethod(EntityNodePath path) throws Exception {
	}
	
	protected void addUpdateMethod(EntityNodePath path) throws Exception {
	}
	
	protected void addCreateMethod(EntityNodePath path) throws Exception {
	}
	
	protected void addDeleteMethod(EntityNodePath path) throws Exception {
	}
	
	private class ResourcePath {
		
		private EntityNodePath nodePath;
		
		private boolean bulk;
		
		private String action;

		public ResourcePath(EntityNodePath nodePath, boolean bulk, String action) {
			this.nodePath = nodePath;
			this.bulk = bulk;
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
		public String getAction() {
			return action;
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
