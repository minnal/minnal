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
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

import org.apache.velocity.Template;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.util.JavassistUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * @author ganeshs
 *
 */
public class CreateMethodCreator extends AbstractMethodCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateMethodCreator.class);
	
	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 */
	public CreateMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath) {
		super(ctClass, resource, resourcePath, basePath);
	}

	private static Template createMethodTemplate = engine.getTemplate("META-INF/templates/create_method.vm");

	@Override
	protected Template getTemplate() {
		return createMethodTemplate;
	}

	@Override
	protected void addParamAnnotations(CtMethod ctMethod) {
		Annotation[][] annotations = new Annotation[4][1];
		Annotation headersParam = new Annotation(Context.class.getCanonicalName(), ctMethod.getMethodInfo().getConstPool());
		annotations[0][0] = headersParam;
		Annotation uriInfoParam = new Annotation(Context.class.getCanonicalName(), ctMethod.getMethodInfo().getConstPool());
		annotations[1][0] = uriInfoParam;
		Annotation providersParam = new Annotation(Context.class.getCanonicalName(), ctMethod.getMethodInfo().getConstPool());
		annotations[2][0] = providersParam;
		Annotation apiParam = new Annotation(ApiParam.class.getCanonicalName(), ctMethod.getMethodInfo().getConstPool());
		apiParam.addMemberValue("name", new StringMemberValue("body", ctMethod.getMethodInfo().getConstPool()));
		apiParam.addMemberValue("access", new StringMemberValue("internal", ctMethod.getMethodInfo().getConstPool()));
		apiParam.addMemberValue("paramType", new StringMemberValue("body", ctMethod.getMethodInfo().getConstPool()));
		annotations[3][0] = apiParam;
		JavassistUtils.addParameterAnnotation(ctMethod, annotations);
	}

	@Override
	protected Annotation getApiOperationAnnotation() {
		ConstPool constPool = getCtClass().getClassFile().getConstPool();
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		Annotation annotation = new Annotation(ApiOperation.class.getCanonicalName(), constPool);
		annotation.addMemberValue("value", new StringMemberValue("Create " + metaData.getName(), constPool));
		annotation.addMemberValue("response", new ClassMemberValue(metaData.getEntityClass().getCanonicalName(), constPool));
		return annotation;
	}

	@Override
	protected List<Annotation> getApiAdditionalParamAnnotations() {
		List<Annotation> annotations = super.getApiAdditionalParamAnnotations();
		annotations.add(getBodyParamAnnotation());
		return annotations;
	}
	
	protected Annotation getBodyParamAnnotation() {
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		Annotation annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), getCtClass().getClassFile().getConstPool());
		annotation.addMemberValue("name", new StringMemberValue("body", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("paramType", new StringMemberValue("body", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("dataType", new StringMemberValue(metaData.getEntityClass().getCanonicalName(), getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("value", new StringMemberValue(metaData.getName() + " payload", getCtClass().getClassFile().getConstPool()));
		return annotation;
	}
	
	@Override
	protected Set<String> getPermissions() {
		return getPermissions(HttpMethod.POST);
	}
	
	@Override
	protected List<Annotation> getApiResponseAnnotations() {
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		return Lists.newArrayList(getOkResponseAnnotation(metaData.getEntityClass()), getBadRequestResponseAnnotation());
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected Class<?> getHttpAnnotation() {
		return POST.class;
	}
}
