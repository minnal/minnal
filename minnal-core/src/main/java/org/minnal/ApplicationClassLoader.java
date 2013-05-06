/**
 * 
 */
package org.minnal;

import javassist.CtClass;

/**
 * @author ganeshs
 *
 */
public class ApplicationClassLoader extends AbstractClassLoader {
	
	public ApplicationClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	protected Class<?> customLoad(CtClass ctClass, boolean resolve) {
		return null;
	}
	
	@Override
	protected boolean shouldExclude(String classname) {
//		if (classname.equals("org.minnal.core.Application")) {
//			return true;
//		}
		return super.shouldExclude(classname);
	}
}
