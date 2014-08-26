/**
 * 
 */
package org.minnal.instrument.resource.creator;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.javalite.common.Inflector;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.PermissionMetaData;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMethodMetaData;
import org.minnal.instrument.util.JavassistUtils;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.route.QueryParam;
import org.minnal.utils.route.RoutePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractMethodCreator {
	
	protected final static VelocityEngine engine;
	
	static {
		Properties properties = new Properties();
		properties.put("runtime.log.logsystem.class", "org.minnal.utils.Slf4jLogChute");
		engine = new VelocityEngine(properties);
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	}
	
	private CtClass ctClass;
	
	private ResourceMetaData resource;
	
	private ResourcePath resourcePath;
	
	private String basePath;
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractMethodCreator.class);

	/**
	 * @param ctClass
	 * @param resource
	 * @param resourcePath
	 * @param basePath
	 */
	public AbstractMethodCreator(CtClass ctClass, ResourceMetaData resource, ResourcePath resourcePath, String basePath) {
		this.ctClass = ctClass;
		this.resource = resource;
		this.resourcePath = resourcePath;
		this.basePath = basePath;
	}
	
	/**
	 * @return
	 */
	public CtClass getCtClass() {
		return ctClass;
	}
	
	/**
	 * @return
	 */
	public ResourcePath getResourcePath() {
		return resourcePath;
	}
	
	/**
	 * @return the resource
	 */
	public ResourceMetaData getResource() {
		return resource;
	}

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Checks if the method already exists in the class
	 * 
	 * @return
	 */
	protected boolean shouldCreate() {
		if (resource == null) {
			return true;
		}
		RoutePattern pattern = getRoutePattern();
		for (ResourceMethodMetaData resourceMethod : resource.getAllResourceMethods()) {
			if (resourceMethod.getPattern().equals(pattern) && resourceMethod.getHttpMethod().equalsIgnoreCase(getHttpMethod())) {
				return false;
			}
		}
		return true;
	}
	
	protected Class<?>[] getParams(CtMethod ctMethod) throws NotFoundException, CannotCompileException {
		Class<?>[] params = new Class<?>[ctMethod.getParameterTypes().length];
		for (int i = 0; i < ctMethod.getParameterTypes().length; i++)  {
			params[i] = ctClass.getClassPool().toClass(ctMethod.getParameterTypes()[i]);
		}
		return params;
	}

	/**
	 * Creates the method
	 * 
	 * @return
	 * @throws CannotCompileException 
	 */
	public CtMethod create() throws CannotCompileException {
		if (! shouldCreate()) {
			return null;
		}
		
		VelocityContext context = new VelocityContext();
		String methodBody = createMethodBody(context);
		return makeMethod(methodBody);
	}
	
	protected RoutePattern getRoutePattern() {
		if (resourcePath.isBulk()) {
			return new RoutePattern(resourcePath.getBulkPath());
		}
		return new RoutePattern(resourcePath.getSinglePath());
	}
	
	/**
	 * Creates the method body
	 * 
	 * @param context
	 * @return
	 */
	protected String createMethodBody(VelocityContext context) {
		context.put("inflector", Inflector.class);
		context.put("path", resourcePath.getNodePath());
		context.put("param_names", getRoutePattern().getParameterNames());
		
		Template template = getTemplate();
		StringWriter writer = new StringWriter();
		
		logger.debug("Creating the method body with context {} and template {} for the resource path {} and method {}", context, template.getName(), resourcePath, getHttpMethod());
		template.merge(context, writer);
		return writer.toString();
	}
	
	/**
	 * Returns the relative path of this method from the root
	 * 
	 * @return
	 */
	protected String getRelativePath() {
		String path = resourcePath.isAction() ? resourcePath.getActionPath() : resourcePath.isBulk() ? resourcePath.getBulkPath() : resourcePath.getSinglePath();
		return HttpUtil.deriveRelativePath(this.basePath, path);
	}
	
	/**
	 * Create the method from the method body
	 * 
	 * @param body
	 * @return
	 * @throws CannotCompileException
	 */
	protected CtMethod makeMethod(String body) throws CannotCompileException {
		logger.trace("Adding the method {} to the class {}", body, ctClass);
		
		CtMethod ctMethod = CtMethod.make(body, ctClass);
		addAnnotations(ctMethod);
		addParamAnnotations(ctMethod);
		getCtClass().addMethod(ctMethod);
		return ctMethod;
	}
	
	/**
	 * Adds the annotations to the method parameters
	 * 
	 * @param ctMethod
	 */
	protected abstract void addParamAnnotations(CtMethod ctMethod);
	
	/**
	 * Adds the annotations to the method
	 * 
	 * @param ctMethod
	 */
	protected void addAnnotations(CtMethod ctMethod) {
		JavassistUtils.addMethodAnnotations(ctMethod, getMethodAnnotation(), getPathAnnotation(), getApiOperationAnnotation(), 
				getApiParamAnnotations(), getSecurityAnnotation(), getApiResponsesAnnotation(), getProducesAnnotation());
	}
	
	/**
	 * Returns the #{@link GET} / #{@link POST} / #{@link PUT} / #{@link DELETE} annotation
	 *   
	 * @return
	 */
	protected Annotation getMethodAnnotation() {
		return new Annotation(getHttpAnnotation().getCanonicalName(), ctClass.getClassFile().getConstPool());
	}
	
	/**
	 * Returns the #{@link Path} annotation
	 * 
	 * @return
	 */
	protected Annotation getPathAnnotation() {
		String relativePath = getRelativePath();
		if (Strings.isNullOrEmpty(relativePath)) {
			return null;
		}
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		Annotation pathAnnotation = new Annotation(Path.class.getCanonicalName(), constPool);
		pathAnnotation.addMemberValue("value", new StringMemberValue(relativePath, constPool));
		return pathAnnotation;
	}
	
	/**
	 * Returns the #{@link ApiOperation} annotation
	 * 
	 * @return
	 */
	protected abstract Annotation getApiOperationAnnotation();
	
	/**
	 * Returns the api path parameter annotations
	 * 
	 * @return
	 */
	protected List<Annotation> getApiPathParamAnnotations() {
		List<Annotation> annotations = new ArrayList<Annotation>();
		List<String> parameters = getRoutePattern().getParameterNames();
		for (int i = 0; i < parameters.size(); i++) {
			Annotation annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
			annotation.addMemberValue("name", new StringMemberValue(parameters.get(i), ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("paramType", new StringMemberValue("path", ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("dataType", new StringMemberValue(String.class.getCanonicalName(), ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("value", new StringMemberValue("The " + getResourcePath().getNodePath().get(i).getEntityMetaData().getName() + " identifier", ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("required", new BooleanMemberValue(true, ctClass.getClassFile().getConstPool()));
			if (i == parameters.size() - 1) {
				annotation.addMemberValue("allowMultiple", new BooleanMemberValue(true, ctClass.getClassFile().getConstPool()));
			}
			annotations.add(annotation);
		}
		return annotations;
	}
	
	/**
	 * Returns the api query parameter annotations
	 * 
	 * @return
	 */
	protected List<Annotation> getApiQueryParamAnnotations() {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (QueryParam param : getResourcePath().getNodePath().getQueryParams()) {
			Annotation annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
			annotation.addMemberValue("name", new StringMemberValue(param.getName(), ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("paramType", new StringMemberValue("query", ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("dataType", new StringMemberValue(param.getType().name(), ctClass.getClassFile().getConstPool()));
			annotation.addMemberValue("value", new StringMemberValue(param.getDescription(), ctClass.getClassFile().getConstPool()));
			annotations.add(annotation);
		}
		return annotations;
	}
	
	protected List<Annotation> getApiAdditionalParamAnnotations() {
		List<Annotation> annotations = new ArrayList<Annotation>();
		// Exclude params
		Annotation annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
		annotation.addMemberValue("name", new StringMemberValue("exclude", ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("paramType", new StringMemberValue("query", ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("dataType", new StringMemberValue(String.class.getCanonicalName(), ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("value", new StringMemberValue("Comma seperated fields to exclude from the response", ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("allowMultiple", new BooleanMemberValue(true, ctClass.getClassFile().getConstPool()));
		annotations.add(annotation);
		
		// Exclude params
		annotation = new Annotation(ApiImplicitParam.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
		annotation.addMemberValue("name", new StringMemberValue("include", ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("paramType", new StringMemberValue("query", ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("dataType", new StringMemberValue(String.class.getCanonicalName(), ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("value", new StringMemberValue("Comma seperated fields to include in the response", ctClass.getClassFile().getConstPool()));
		annotation.addMemberValue("allowMultiple", new BooleanMemberValue(true, ctClass.getClassFile().getConstPool()));
		annotations.add(annotation);
		
		return annotations;
	}
	
	/**
	 * Returns the api parameter annotations
	 * 
	 * @return
	 */
	protected Annotation getApiParamAnnotations() {
		Annotation implicitParams = new Annotation(ApiImplicitParams.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
		List<AnnotationMemberValue> annotationMemberValues = new ArrayList<AnnotationMemberValue>();
		List<Annotation> annotations = Lists.newArrayList();
		annotations.addAll(getApiPathParamAnnotations());
		annotations.addAll(getApiQueryParamAnnotations());
		annotations.addAll(getApiAdditionalParamAnnotations());
		for (Annotation annotation : annotations) {
			annotationMemberValues.add(new AnnotationMemberValue(annotation, ctClass.getClassFile().getConstPool()));
		}
		ArrayMemberValue values = new ArrayMemberValue(ctClass.getClassFile().getConstPool());
		values.setValue(annotationMemberValues.toArray(new AnnotationMemberValue[0]));
		implicitParams.addMemberValue("value", values);
		return implicitParams;
	}
	
	/**
	 * Returns the security annotation
	 * 
	 * @return
	 */
	protected Annotation getSecurityAnnotation() {
		Set<String> permissions = getPermissions();
		if (permissions == null || permissions.isEmpty()) {
			return null;
		}
		
		Annotation rolesAllowed = new Annotation(RolesAllowed.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
		ArrayMemberValue values = new ArrayMemberValue(ctClass.getClassFile().getConstPool());
		List<StringMemberValue> memberValues = new ArrayList<StringMemberValue>();
		for (String permission : permissions) {
			memberValues.add(new StringMemberValue(permission, ctClass.getClassFile().getConstPool()));
		}
		values.setValue(memberValues.toArray(new StringMemberValue[0]));
		rolesAllowed.addMemberValue("value", values);
		return rolesAllowed;
	}
	
	protected Annotation getApiResponsesAnnotation() {
		Annotation apiResponses = new Annotation(ApiResponses.class.getCanonicalName(), ctClass.getClassFile().getConstPool());
		ArrayMemberValue values = new ArrayMemberValue(ctClass.getClassFile().getConstPool());
		List<AnnotationMemberValue> memberValues = new ArrayList<AnnotationMemberValue>();
		for (Annotation annotation : getApiResponseAnnotations()) {
			memberValues.add(new AnnotationMemberValue(annotation, ctClass.getClassFile().getConstPool()));
		}
		values.setValue(memberValues.toArray(new AnnotationMemberValue[0]));
		apiResponses.addMemberValue("value", values);
		return apiResponses;
	}
	
	protected Annotation getProducesAnnotation() {
		Annotation annotation = new Annotation(Produces.class.getCanonicalName(), getCtClass().getClassFile().getConstPool());
		ArrayMemberValue values = new ArrayMemberValue(getCtClass().getClassFile().getConstPool());
		StringMemberValue json = new StringMemberValue(MediaType.APPLICATION_JSON, getCtClass().getClassFile().getConstPool());
		StringMemberValue xml = new StringMemberValue(MediaType.APPLICATION_XML, getCtClass().getClassFile().getConstPool());
		values.setValue(new StringMemberValue[]{json, xml});
		annotation.addMemberValue("value", values);
		return annotation;
	}
	
	protected abstract List<Annotation> getApiResponseAnnotations();
	
	protected abstract String getHttpMethod();
	
	protected abstract Class<?> getHttpAnnotation();
	
	/**
	 * Returns the permissions required to access this method
	 * 
	 * @return
	 */
	protected abstract Set<String> getPermissions();
	
	/**
	 * Returns the permissions for the given http method
	 * 
	 * @return
	 */
	protected Set<String> getPermissions(String httpMethod) {
		Set<PermissionMetaData> permissions = Sets.newHashSet();
		if (resourcePath.getNodePath().size() == 1) {
			permissions = resourcePath.getNodePath().get(0).getEntityMetaData().getPermissionMetaData();
		} else {
			CollectionMetaData source = resourcePath.getNodePath().get(resourcePath.getNodePath().size() - 1).getSource();
			if (source != null) {
				permissions = source.getPermissionMetaData();
			}
		}
		for (PermissionMetaData permission : permissions) {
			if (permission.getMethod().equalsIgnoreCase(httpMethod)) {
				return permission.getPermissions();
			}
		}
		return Sets.newHashSet();
	}
	
	/**
	 * Returns the template that will be used to create the method
	 * 
	 * @return
	 */
	protected abstract Template getTemplate();
	
	/**
	 * Returns the 200 ok response annotation
	 * 
	 * @param responseClass
	 * @return
	 */
	protected Annotation getOkResponseAnnotation(Class<?> responseClass) {
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiResponse.class.getCanonicalName(), constPool);
		IntegerMemberValue code = new IntegerMemberValue(constPool);
		code.setValue(Response.Status.OK.getStatusCode());
		annotation.addMemberValue("code", code);
		annotation.addMemberValue("message", new StringMemberValue(Response.Status.OK.getReasonPhrase(), constPool));
		annotation.addMemberValue("response", new ClassMemberValue(responseClass.getCanonicalName(), constPool));
		return annotation;
	}
	
	/**
	 * Returns the 404 not found response annotation
	 * 
	 * @param responseClass
	 * @return
	 */
	protected Annotation getNotFoundResponseAnnotation() {
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiResponse.class.getCanonicalName(), constPool);
		IntegerMemberValue code = new IntegerMemberValue(constPool);
		code.setValue(Response.Status.NOT_FOUND.getStatusCode());
		annotation.addMemberValue("code", code);
		annotation.addMemberValue("message", new StringMemberValue(Response.Status.NOT_FOUND.getReasonPhrase(), constPool));
		return annotation;
	}
	
	/**
	 * Returns the 204 no content response annotation
	 * 
	 * @param responseClass
	 * @return
	 */
	protected Annotation getNoContentResponseAnnotation() {
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiResponse.class.getCanonicalName(), constPool);
		IntegerMemberValue code = new IntegerMemberValue(constPool);
		code.setValue(Response.Status.NO_CONTENT.getStatusCode());
		annotation.addMemberValue("code", code);
		annotation.addMemberValue("message", new StringMemberValue(Response.Status.NO_CONTENT.getReasonPhrase(), constPool));
		return annotation;
	}
	
	/**
	 * Returns the 400 bad request response annotation
	 * 
	 * @param responseClass
	 * @return
	 */
	protected Annotation getBadRequestResponseAnnotation() {
		ConstPool constPool = ctClass.getClassFile().getConstPool();
		Annotation annotation = new Annotation(ApiResponse.class.getCanonicalName(), constPool);
		IntegerMemberValue code = new IntegerMemberValue(constPool);
		code.setValue(Response.Status.BAD_REQUEST.getStatusCode());
		annotation.addMemberValue("code", code);
		annotation.addMemberValue("message", new StringMemberValue(Response.Status.BAD_REQUEST.getReasonPhrase(), constPool));
		return annotation;
	}
}
