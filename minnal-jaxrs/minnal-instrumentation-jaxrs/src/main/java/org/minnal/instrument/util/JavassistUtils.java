/**
 * 
 */
package org.minnal.instrument.util;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

/**
 * @author ganeshs
 *
 */
public class JavassistUtils {

	/**
	 * Adds the annotations to the clazz
	 * 
	 * @param clazz
	 * @param annotations
	 */
	public static void addClassAnnotations(CtClass clazz, Annotation... annotations) {
		ConstPool constPool = clazz.getClassFile().getConstPool();
		AnnotationsAttribute attr = (AnnotationsAttribute) clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
		if (attr == null) {
			attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			clazz.getClassFile().addAttribute(attr);
		}
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation != null) {
					attr.addAnnotation(annotation);
				}
			}
		}
	}
	
	/**
	 * Adds the annotations to the method parameters
	 * 
	 * @param ctMethod
	 * @param annotations
	 */
	public static void addParameterAnnotation(CtMethod ctMethod, Annotation[][] annotations) {
		MethodInfo methodInfo = ctMethod.getMethodInfo();
		ParameterAnnotationsAttribute paramAtrributeInfo = (ParameterAnnotationsAttribute) methodInfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
		if (paramAtrributeInfo == null) {
			paramAtrributeInfo = new ParameterAnnotationsAttribute(methodInfo.getConstPool(), ParameterAnnotationsAttribute.visibleTag);
			methodInfo.addAttribute(paramAtrributeInfo);
		}
		paramAtrributeInfo.setAnnotations(annotations);
	}
	
	/**
	 * Adds the given annotations to the method
	 * 
	 * @param ctMethod
	 * @param annotations
	 */
	public static void addMethodAnnotations(CtMethod ctMethod, Annotation... annotations) {
		if (annotations == null) {
			return;
		}
		MethodInfo methodInfo = ctMethod.getMethodInfo();
		AnnotationsAttribute attr = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
		if (attr == null) {
			attr = new AnnotationsAttribute(methodInfo.getConstPool(), AnnotationsAttribute.visibleTag);
			methodInfo.addAttribute(attr);
		}
		for (Annotation annotation : annotations) {
			if (annotation != null) {
				attr.addAnnotation(annotation);
			}
		}
	}
}
