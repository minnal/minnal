/**
 * 
 */
package org.minnal.instrument.resource.creator;

import java.util.List;

import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.velocity.Template;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.resource.ResourceWrapper.HTTPMethod;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author ganeshs
 *
 */
public class ListMethodCreator extends ReadMethodCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(ListMethodCreator.class);
	
	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 * @param httpMethod
	 */
	public ListMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath, HTTPMethod httpMethod) {
		super(ctClass, resource, resourcePath, basePath, httpMethod);
	}

	private static Template listMethodTemplate = engine.getTemplate("META-INF/templates/list_method.vm");

	@Override
	protected Template getTemplate() {
		return listMethodTemplate;
	}

	@Override
	protected Annotation getApiOperationAnnotation() {
		ConstPool constPool = getCtClass().getClassFile().getConstPool();
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		Annotation annotation = new Annotation(ApiOperation.class.getCanonicalName(), constPool);
		annotation.addMemberValue("value", new StringMemberValue("Search " + metaData.getName(), constPool));
		annotation.addMemberValue("response", new ClassMemberValue(metaData.getEntityClass().getCanonicalName(), constPool));
		annotation.addMemberValue("responseContainer", new StringMemberValue("List", constPool));
		return annotation;
	}

	@Override
	protected List<Annotation> getApiAdditionalParamAnnotations() {
		List<Annotation> annotations = super.getApiAdditionalParamAnnotations();
		// per_page param annotation
		Annotation annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), getCtClass().getClassFile().getConstPool());
		annotation.addMemberValue("name", new StringMemberValue("per_page", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("paramType", new StringMemberValue("query", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("dataType", new StringMemberValue(Integer.class.getCanonicalName(), getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("value", new StringMemberValue("No of results per page", getCtClass().getClassFile().getConstPool()));
		annotations.add(annotation);

		// page param annotation
		annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), getCtClass().getClassFile().getConstPool());
		annotation.addMemberValue("name", new StringMemberValue("page", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("paramType", new StringMemberValue("query", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("dataType", new StringMemberValue(Integer.class.getCanonicalName(), getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("value", new StringMemberValue("Current page number", getCtClass().getClassFile().getConstPool()));
		annotations.add(annotation);
		return annotations;
	}
	
	@Override
	protected List<Annotation> getApiResponseAnnotations() {
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		return Lists.newArrayList(getOkResponseAnnotation(metaData.getEntityClass()));
	}
}
