/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.StringWriter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.minnal.core.Request;
import org.minnal.core.Response;

/**
 * @author ganeshs
 *
 */
public abstract class MethodCreator {

	private CtClass ctClass;
	
	public MethodCreator(CtClass ctClass) {
		this.ctClass = ctClass;
	}
	
	public void create() throws Exception {
		createMethod(createBody());
	}
	
	protected abstract String createBody();
	
	protected void createMethod(String body) throws Exception {
		String methodName = getMethodName();
		if (methodExists(methodName)) {
			return;
		}
		if (ctClass.isFrozen()) {
			ctClass.defrost();
		}
		StringWriter writer = new StringWriter();
		writer.append("public Object ").append(methodName).append("(").append(Request.class.getName()).append(" request, ");
		writer.append(Response.class.getName()).append(" response) {").append(body).append("}");
		CtMethod method = CtNewMethod.make(writer.toString(), ctClass);
		ctClass.addMethod(method);
	}
	
	public abstract String getMethodName();
	
	protected boolean methodExists(String methodName) {
		try {
			ctClass.getDeclaredMethod(methodName, new CtClass[]{ClassPool.getDefault().get(Request.class.getName()), 
					ClassPool.getDefault().get(Request.class.getName())});
			return true;
		} catch (NotFoundException e) {
			return false;
		}
	}
}
