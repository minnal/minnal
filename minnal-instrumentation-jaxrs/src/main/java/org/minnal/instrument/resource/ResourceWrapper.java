/**
 * 
 */
package org.minnal.instrument.resource;

import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

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
import org.minnal.instrument.resource.creator.ResourceClassCreator;
import org.minnal.instrument.resource.creator.UpdateMethodCreator;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

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
	 * @param resource
	 * @param entityClass
	 */
	public ResourceWrapper(ResourceMetaData resource, Class<?> entityClass) {
		this.resource = resource;
		this.entityClass = entityClass;
		init();
	}
	
	/**
	 * Initializes the wrapper
	 */
	protected void init() {
		this.path = resource != null ? resource.getPath() : namingStrategy.getResourceName(namingStrategy.getEntityName(entityClass));
		this.generatedClass = getResourceClassCreator().create();
	}
	
	/**
	 * Returns the resource class creator
	 * 
	 * @return
	 */
	protected ResourceClassCreator getResourceClassCreator() {
		return new ResourceClassCreator(resource, entityClass, path);
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
				addActionMethods(new ResourcePath(path, false));
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
		return new ReadMethodCreator(generatedClass, resource, path, this.path);
	}
	
	protected ListMethodCreator getListMethodCreator(ResourcePath path) {
		return new ListMethodCreator(generatedClass, resource, path, this.path);
	}
	
	protected CreateMethodCreator getCreateMethodCreator(ResourcePath path) {
		return new CreateMethodCreator(generatedClass, resource, path, this.path);
	}
	
	protected UpdateMethodCreator getUpdateMethodCreator(ResourcePath path) {
		return new UpdateMethodCreator(generatedClass, resource, path, this.path);
	}
	
	protected DeleteMethodCreator getDeleteMethodCreator(ResourcePath path) {
		return new DeleteMethodCreator(generatedClass, resource, path, this.path);
	}
	
	protected ActionMethodCreator getActionMethodCreator(ResourcePath path, ActionMetaData action) {
		return new ActionMethodCreator(generatedClass, resource, path, this.path, action);
	}
	
	protected void addActionMethods(ResourcePath resourcePath) throws Exception {
		logger.debug("Adding the action methods for the resource path {} and method {}", resourcePath);
		if (resourcePath.getNodePath().size() > 1) {
			logger.debug("Not adding the action methods for the non root paths. Resource path - {} and method - {}", resourcePath);
			// Actions will be added only for the root paths
			return;
		}
		Set<ActionMetaData> actions = resourcePath.getNodePath().get(0).getEntityMetaData().getActionMethods();
		EntityNode node = resourcePath.getNodePath().get(0);
		ResourcePath actionPath = null;
		for (ActionMetaData action : actions) {
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
	 * @return the generatedClass
	 */
	public CtClass getGeneratedClass() {
		return generatedClass;
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
}
