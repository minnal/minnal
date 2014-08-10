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

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
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
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author ganeshs
 *
 */
public class ReadMethodCreator extends AbstractMethodCreator {
	
	private static final Logger logger = LoggerFactory.getLogger(ReadMethodCreator.class);
	
	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 */
	public ReadMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath) {
		super(ctClass, resource, resourcePath, basePath);
	}

	private static Template readMethodTemplate = engine.getTemplate("META-INF/templates/read_method.vm");

	@Override
	protected Template getTemplate() {
		return readMethodTemplate;
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
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		Annotation annotation = new Annotation(ApiOperation.class.getCanonicalName(), constPool);
		annotation.addMemberValue("value", new StringMemberValue("Find " + getResourcePath().getNodePath().getName() + " by id", constPool));
		annotation.addMemberValue("response", new ClassMemberValue(metaData.getEntityClass().getCanonicalName(), constPool));
		return annotation;
	}
	
	@Override
	protected Set<String> getPermissions() {
		return getPermissions(HttpMethod.GET);
	}
	
	@Override
	protected List<Annotation> getApiResponseAnnotations() {
		EntityNodePath path = getResourcePath().getNodePath();
		EntityMetaData metaData = path.get(path.size() - 1).getEntityMetaData();
		return Lists.newArrayList(getOkResponseAnnotation(metaData.getEntityClass()), getNotFoundResponseAnnotation());
	}

	@Override
	protected String getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected Class<?> getHttpAnnotation() {
		return GET.class;
	}
}
