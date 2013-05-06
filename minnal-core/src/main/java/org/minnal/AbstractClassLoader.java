/**
 * 
 */
package org.minnal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractClassLoader extends ClassLoader {
	
	private ClassPool classPool = ClassPool.getDefault();
	
	private List<String> excludedPackages = new ArrayList<String>(Arrays.asList("java.", "javax.", "sun.", "org.mockito"));
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractClassLoader.class);

	public AbstractClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		logger.trace("Loading the class " + name);
		if (name.equals("org.minnal.core.resource.ResourceClass")) {
			System.out.println("");
		}
		if (shouldExclude(name)) {
			return super.loadClass(name, resolve);
		}
		Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			logger.trace("Class " + name + " is already loaded");
			return clazz;
		}
		
		byte[] bytes = null;
		try {
			CtClass ctClass = classPool.get(name);
			clazz = customLoad(ctClass, resolve);
			if (clazz != null) {
				return clazz;
			}
			bytes = ctClass.toBytecode();
		} catch (Exception e) {
			logger.trace("Failed while loading the class - " + name, e);
			return super.loadClass(name, resolve);
		}
		
		try {
			clazz = defineClass(name, bytes, 0, bytes.length);
		} catch (LinkageError error) {
			return super.loadClass(name, resolve);
		}
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}
	
	protected abstract Class<?> customLoad(CtClass ctClass, boolean resolve);
	
	protected List<String> getExcludedPackages() {
		return excludedPackages;
	}
	
	protected ClassPool getClassPool() {
		return classPool;
	}
	
	protected boolean shouldExclude(String classname) {
		for (String excluded : excludedPackages) {
			if (classname.startsWith(excluded)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isExtendingClass(CtClass ctClass, CtClass superClass) throws NotFoundException {
		return getSuperClasses(ctClass).contains(superClass);
	}
	
	/**
	 * Returns the super classes from top to bottom. The {@link Object} class name will always be returned at index 0.
	 * 
	 * @param ctClass
	 * @return
	 * @throws IOException
	 */
	protected List<CtClass> getSuperClasses(CtClass ctClass) throws NotFoundException {
		List<CtClass> superClasses = new ArrayList<CtClass>();
		CtClass superClass = getSuperClass(ctClass);
		if (superClass != null) {
			superClasses.addAll(getSuperClasses(superClass));
			superClasses.add(superClass);
		}
		return superClasses;
	}
	
	protected CtClass getSuperClass(CtClass ctClass) throws NotFoundException {
		return ctClass.getSuperclass();
	}
}
