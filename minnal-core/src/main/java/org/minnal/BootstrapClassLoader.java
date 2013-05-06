/**
 * 
 */
package org.minnal;

import java.util.HashMap;
import java.util.Map;

import javassist.CtClass;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class BootstrapClassLoader extends AbstractClassLoader {
	
	private Map<String, ApplicationClassLoader> appClassLoaders = new HashMap<String, ApplicationClassLoader>();
	
	private CtClass applicationClass;
	
	private static final Logger logger = LoggerFactory.getLogger(BootstrapClassLoader.class);
	
	public BootstrapClassLoader(ClassLoader parent) {
		super(parent);
		try {
			applicationClass = getClassPool().get("org.minnal.core.Application");
		} catch (NotFoundException e) {
			// shouldn't get here.
			logger.error("Class org.minnal.core.Application is not found", e);
			throw new IllegalStateException("Class org.minnal.core.Application is not found");
		}
	}

	protected Class<?> customLoad(CtClass ctClass, boolean resolve) {
		String name = ctClass.getName();
		try {
			if (isExtendingClass(ctClass, applicationClass)) {
				ApplicationClassLoader classLoader = appClassLoaders.get(name);
				if (classLoader == null) {
					classLoader = new ApplicationClassLoader(this);
					appClassLoaders.put(name, classLoader);
				}
				return classLoader.loadClass(name, resolve);
			}
		} catch (Exception e) {
			logger.trace("Failed while loading the class - " + name, e);
		}
		return null;
	}
	
	@Override
	protected boolean shouldExclude(String classname) {
//		if (classname.equals(applicationClass.getName())) {
//			return true;
//		}
		return super.shouldExclude(classname);
	}
}
