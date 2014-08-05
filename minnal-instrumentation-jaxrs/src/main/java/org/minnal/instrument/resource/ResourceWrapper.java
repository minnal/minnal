/**
 * 
 */
package org.minnal.instrument.resource;

import java.util.List;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
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

import org.javalite.common.Inflector;
import org.minnal.instrument.DefaultNamingStrategy;
import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.resource.creator.ActionMethodCreator;
import org.minnal.instrument.resource.creator.CreateMethodCreator;
import org.minnal.instrument.resource.creator.DeleteMethodCreator;
import org.minnal.instrument.resource.creator.ListMethodCreator;
import org.minnal.instrument.resource.creator.ReadMethodCreator;
import org.minnal.instrument.resource.creator.UpdateMethodCreator;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.wordnik.swagger.annotations.Api;

/**
 * @author ganeshs
 *
 */
public class ResourceWrapper {
	
	private CtClass generatedClass;
	
	private ClassPool classPool = ClassPool.getDefault();
	
	private ResourceMetaData resource;
	
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
		this.generatedClass = createGeneratedClass();
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
		Annotation pathAnnotation = new Annotation(Path.class.getCanonicalName(), constPool);
		pathAnnotation.addMemberValue("value", new StringMemberValue(path, constPool));
		
		Annotation apiAnnotation = new Annotation(Api.class.getCanonicalName(), constPool);
		apiAnnotation.addMemberValue("value", new StringMemberValue(path, constPool));
		
		addClassAnnotation(generatedClass, apiAnnotation);
		addClassAnnotation(generatedClass, pathAnnotation);
		
		return generatedClass;
	}
	
	/**
	 * Adds the annotation to the clazz
	 * 
	 * @param clazz
	 * @param annotation
	 */
	protected void addClassAnnotation(CtClass clazz, Annotation annotation) {
		ConstPool constPool = clazz.getClassFile().getConstPool();
		AnnotationsAttribute attr = (AnnotationsAttribute) clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
		if (attr == null) {
			attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			clazz.getClassFile().addAttribute(attr);
		}
		attr.addAnnotation(annotation);
	}
	
	public void addPath(EntityNodePath path) {
		logger.debug("Adding the path {}", path);
		try {
			if (path.isReadAllowed()) { 
				getReadMethodCreator(new ResourcePath(path, false)).create();
				getListMethodCreator(new ResourcePath(path, true)).create();
			}
			if (path.isCreateAllowed()) {
				getCreateMethodCreator(new ResourcePath(path, true)).create();
			}
			if (path.isUpdateAllowed()) {
				getUpdateMethodCreator(new ResourcePath(path, false)).create();
				addActionMethods(new ResourcePath(path, false), HTTPMethod.put);
			}
			if (path.isDeleteAllowed()) {
				getDeleteMethodCreator(new ResourcePath(path, false)).create();
			}
		} catch (Exception e) {
			logger.error("Failed while adding the path", e);
			throw new MinnalInstrumentationException(e);
		}
	}
	
	protected ReadMethodCreator getReadMethodCreator(ResourcePath path) {
		return new ReadMethodCreator(generatedClass, resource, path, this.path, HTTPMethod.get);
	}
	
	protected ListMethodCreator getListMethodCreator(ResourcePath path) {
		return new ListMethodCreator(generatedClass, resource, path, this.path, HTTPMethod.get);
	}
	
	protected CreateMethodCreator getCreateMethodCreator(ResourcePath path) {
		return new CreateMethodCreator(generatedClass, resource, path, this.path, HTTPMethod.post);
	}
	
	protected UpdateMethodCreator getUpdateMethodCreator(ResourcePath path) {
		return new UpdateMethodCreator(generatedClass, resource, path, this.path, HTTPMethod.put);
	}
	
	protected DeleteMethodCreator getDeleteMethodCreator(ResourcePath path) {
		return new DeleteMethodCreator(generatedClass, resource, path, this.path, HTTPMethod.delete);
	}
	
	protected ActionMethodCreator getActionMethodCreator(ResourcePath path, ActionMetaData action) {
		return new ActionMethodCreator(generatedClass, resource, path, this.path, HTTPMethod.put, action);
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
			getActionMethodCreator(actionPath, action).create();
		}
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
			return ! Strings.isNullOrEmpty(action);
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
