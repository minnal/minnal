/**
 * 
 */
package org.minnal.instrument.resource.creator;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.Path;

import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.util.JavassistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;

/**
 * @author ganeshs
 *
 */
public class ResourceClassCreator {

	private ResourceMetaData resource;
	
	private Class<?> entityClass;
	
	private String path;
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceClassCreator.class);
	
	private ClassPool classPool = ClassPool.getDefault();
	
	private NamingStrategy namingStrategy;

	/**
	 * @param resource
	 * @param entityClass
	 * @param path
	 */
	public ResourceClassCreator(ResourceMetaData resource, NamingStrategy namingStrategy, Class<?> entityClass, String path) {
		this.resource = resource;
		this.entityClass = entityClass;
		this.path = path;
		this.namingStrategy = namingStrategy;
	}
	
	/**
	 * @return the resource
	 */
	public ResourceMetaData getResource() {
		return resource;
	}

	/**
	 * @return the entityClass
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	public CtClass create() {
		CtClass ctClass = null;
		if (resource != null) {
			logger.debug("Creating the generated class for the resource {}", resource.getResourceClass());
			
			try {
				CtClass superClass = classPool.get(resource.getResourceClass().getName());
				superClass.defrost();
				ctClass = classPool.makeClass(namingStrategy.getResourceWrapperClassName(resource.getResourceClass()), superClass);
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
			ctClass = classPool.makeClass(namingStrategy.getResourceClassName(entityClass));
		}
		
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		JavassistUtils.addClassAnnotations(ctClass, getApiAnnotation(constPool), getPathAnnotation(constPool));
		return ctClass;
	}
	
	/**
	 * Returns the path annotation
	 * 
	 * @param constPool
	 * @return
	 */
	protected Annotation getPathAnnotation(ConstPool constPool) {
		Annotation pathAnnotation = new Annotation(Path.class.getCanonicalName(), constPool);
		pathAnnotation.addMemberValue("value", new StringMemberValue(path, constPool));
		return pathAnnotation;
	}
	
	/**
	 * Returns the api annotation
	 * 
	 * @param constPool
	 * @return
	 */
	protected Annotation getApiAnnotation(ConstPool constPool) {
		Annotation apiAnnotation = new Annotation(Api.class.getCanonicalName(), constPool);
		apiAnnotation.addMemberValue("value", new StringMemberValue(path, constPool));
		apiAnnotation.addMemberValue("description", new StringMemberValue("Operations about " + namingStrategy.getResourceName(entityClass), constPool));
		return apiAnnotation;
	}
}
