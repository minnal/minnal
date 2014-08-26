/**
 * 
 */
package org.minnal.instrument.resource.creator;

import java.util.List;
import java.util.Map;

import javassist.CtClass;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.utils.route.RoutePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author ganeshs
 *
 */
public class ActionMethodCreator extends UpdateMethodCreator {
	
	private ActionMetaData action;
	
	private static final Logger logger = LoggerFactory.getLogger(ActionMethodCreator.class);
	
	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 * @param action
	 */
	public ActionMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath, ActionMetaData action) {
		super(ctClass, resource, resourcePath, basePath);
		this.action = action;
	}

	private static Template actionMethodTemplate = engine.getTemplate("META-INF/templates/action_method.vm");

	@Override
	protected Template getTemplate() {
		return actionMethodTemplate;
	}

	@Override
	protected RoutePattern getRoutePattern() {
		return new RoutePattern(getResourcePath().getActionPath());
	}

	@Override
	protected Annotation getApiOperationAnnotation() {
		ConstPool constPool = getCtClass().getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiOperation.class.getCanonicalName(), constPool);
		annotation.addMemberValue("value", new StringMemberValue("Performs action on " + getResourcePath().getNodePath().getName(), constPool));
		return annotation;
	}
	
	@Override
	protected Annotation getBodyParamAnnotation() {
		Annotation annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), getCtClass().getClassFile().getConstPool());
		annotation.addMemberValue("name", new StringMemberValue("body", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("paramType", new StringMemberValue("body", getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("dataType", new StringMemberValue(Map.class.getCanonicalName(), getCtClass().getClassFile().getConstPool()));
		annotation.addMemberValue("value", new StringMemberValue("Request payload", getCtClass().getClassFile().getConstPool()));
		return annotation;
	}

	@Override
	protected String createMethodBody(VelocityContext context) {
		context.put("action", action);
		return super.createMethodBody(context);
	}
	
	@Override
	protected List<Annotation> getApiResponseAnnotations() {
		List<Annotation> annotations = Lists.newArrayList(getNotFoundResponseAnnotation(), getBadRequestResponseAnnotation());
		if (action.getMethod().getReturnType().equals(Void.class)) {
			annotations.add(getNoContentResponseAnnotation()); 
		} else {
			annotations.add(getOkResponseAnnotation(action.getMethod().getReturnType()));
		}
		return annotations;
	}

	/**
	 * @return the action
	 */
	public ActionMetaData getAction() {
		return action;
	}
}
