/**
 * 
 */
package org.minnal.instrument.resource.creator;

import java.util.List;
import java.util.Set;

import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.HttpMethod;

import org.apache.velocity.Template;
import org.minnal.instrument.resource.ResourceWrapper.HTTPMethod;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author ganeshs
 *
 */
public class UpdateMethodCreator extends CreateMethodCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(UpdateMethodCreator.class);
	
	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 * @param httpMethod
	 */
	public UpdateMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath, HTTPMethod httpMethod) {
		super(ctClass, resource, resourcePath, basePath, httpMethod);
	}

	private static Template updateMethodTemplate = engine.getTemplate("META-INF/templates/update_method.vm");

	@Override
	protected Template getTemplate() {
		return updateMethodTemplate;
	}

	@Override
	protected Annotation getApiOperationAnnotation() {
		ConstPool constPool = getCtClass().getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiOperation.class.getCanonicalName(), constPool);
		annotation.addMemberValue("value", new StringMemberValue("Update " + getResourcePath().getNodePath().getName() + " by id", constPool));
		return annotation;
	}
	
	@Override
	protected Set<String> getPermissions() {
		return getPermissions(HttpMethod.PUT);
	}
	
	@Override
	protected List<Annotation> getApiResponseAnnotations() {
		return Lists.newArrayList(getNoContentResponseAnnotation(), getNotFoundResponseAnnotation(), getBadRequestResponseAnnotation());
	}
}
