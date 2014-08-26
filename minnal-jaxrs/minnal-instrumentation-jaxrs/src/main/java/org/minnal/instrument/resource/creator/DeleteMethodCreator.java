/**
 * 
 */
package org.minnal.instrument.resource.creator;

import java.util.List;
import java.util.Set;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.DELETE;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Context;

import org.apache.velocity.Template;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.util.JavassistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author ganeshs
 *
 */
public class DeleteMethodCreator extends AbstractMethodCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(DeleteMethodCreator.class);
	
	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 */
	public DeleteMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath) {
		super(ctClass, resource, resourcePath, basePath);
	}

	private static Template deleteMethodTemplate = engine.getTemplate("META-INF/templates/delete_method.vm");

	@Override
	protected Template getTemplate() {
		return deleteMethodTemplate;
	}

	@Override
	protected void addParamAnnotations(CtMethod ctMethod) {
		Annotation[][] annotations = new Annotation[1][0];
		Annotation parameterAnnotation = new Annotation(Context.class.getCanonicalName(), ctMethod.getMethodInfo().getConstPool());
		annotations[0] = new Annotation[1];
		annotations[0][0] = parameterAnnotation;
		JavassistUtils.addParameterAnnotation(ctMethod, annotations);
	}

	@Override
	protected Annotation getApiOperationAnnotation() {
		ConstPool constPool = getCtClass().getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiOperation.class.getCanonicalName(), constPool);
		annotation.addMemberValue("value", new StringMemberValue("Delete " + getResourcePath().getNodePath().getName() + " by id", constPool));
		return annotation;
	}

	@Override
	protected Set<String> getPermissions() {
		return getPermissions(HttpMethod.DELETE);
	}
	
	@Override
	protected List<Annotation> getApiResponseAnnotations() {
		return Lists.newArrayList(getNoContentResponseAnnotation(), getNotFoundResponseAnnotation());
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.DELETE;
	}

	@Override
	protected Class<?> getHttpAnnotation() {
		return DELETE.class;
	}
}
