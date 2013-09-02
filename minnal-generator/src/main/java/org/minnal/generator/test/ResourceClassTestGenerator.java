/**
 * 
 */
package org.minnal.generator.test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.javalite.common.Inflector;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.RoutePattern;
import org.minnal.core.util.Node.PathVisitor;
import org.minnal.generator.core.AbstractTemplateGenerator;
import org.minnal.generator.util.CodeUtils;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

/**
 * @author ganeshs
 *
 */
public class ResourceClassTestGenerator extends AbstractTemplateGenerator {
	
	private ResourceClass resourceClass;
	
	private List<EntityNodePath> paths = new ArrayList<EntityNode.EntityNodePath>();
	
	private static Template createMethodTestTemplate = engine.getTemplate("META-INF/templates/create_method_test.vm");
	
	private static Template updateMethodTestTemplate = engine.getTemplate("META-INF/templates/update_method_test.vm");
	
	private static Template readMethodTestTemplate = engine.getTemplate("META-INF/templates/read_method_test.vm");
	
	private static Template listMethodTestTemplate = engine.getTemplate("META-INF/templates/list_method_test.vm");;
	
	private static Template deleteMethodTestTemplate = engine.getTemplate("META-INF/templates/delete_method_test.vm");;
	
	private static Template createResourceTestClassTemplate = engine.getTemplate("META-INF/templates/create_resource_class_test.vm");
	
	/**
	 * @param resourceClass
	 */
	public ResourceClassTestGenerator(String projectDir, ResourceClass resourceClass) {
		super(new File(projectDir));
		this.resourceClass = resourceClass;
	}
	
	public void init() {
		EntityNode tree = new EntityNode(resourceClass.getEntityClass());
		tree.construct();
		tree.traverse(new PathVisitor<EntityNodePath, EntityNode>() {
			@Override
			public void visit(EntityNodePath path) {
				paths.add(path);
			}
		});
		super.init();
	}
	
	@Override
	public void generate() {
		String packageName = resourceClass.getEntityClass().getPackage().getName() + ".generated";
		String testClass = resourceClass.getEntityClass().getSimpleName() + "ResourceTest";
		
		StringBuffer buffer = new StringBuffer();
		for (EntityNodePath path : paths) {
			createMethods(path, buffer);
		}
		
		VelocityContext context = new VelocityContext();
		context.put("inflector", Inflector.class);
		context.put("package_name", packageName);
		context.put("methods", buffer.toString());
		context.put("resource_class", testClass);
		
		StringWriter writer = new StringWriter();
		createResourceTestClassTemplate.merge(context, writer);
		
		writeFile(CodeUtils.format(writer.toString()), new File(createPackage(packageName, TEST_JAVA_FOLDER), testClass + ".java"));
	}

	private void createMethods(EntityNodePath path, StringBuffer buffer) {
		try {
			if (path.isReadAllowed()) {
				buffer.append(createMethod(path, true, HttpMethod.GET)).append("\n");
				buffer.append(createMethod(path, false, HttpMethod.GET)).append("\n");
			}
			if (path.isCreateAllowed()) {
				buffer.append(createMethod(path, true, HttpMethod.POST)).append("\n");
			}
			if (path.isUpdateAllowed()) {
				buffer.append(createMethod(path, false, HttpMethod.PUT)).append("\n");
			}
			if (path.isDeleteAllowed()) {
				buffer.append(createMethod(path, false, HttpMethod.DELETE)).append("\n");
			}
		} catch (Exception e) {
			throw new MinnalException(e);
		}
	}
	
	protected StringWriter createMethod(EntityNodePath path, boolean bulk, HttpMethod method) throws Exception {
		Template template = getMethodTemplate(path, bulk, method);
		if (template == null) {
			// TODO Can't get here. Handle if it still gets here
			return new StringWriter();
		}
		
		VelocityContext context = new VelocityContext();
		context.put("inflector", Inflector.class);
		context.put("generator", this);
		context.put("path", path);
		if (bulk) {
			context.put("param_names", new RoutePattern(path.getBulkPath()).getParameterNames());
		} else {
			context.put("param_names", new RoutePattern(path.getSinglePath()).getParameterNames());
		}
		
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer;
	}

	protected Template getMethodTemplate(EntityNodePath path, boolean bulk, HttpMethod method) {
		if (bulk) {
			if (method.equals(HttpMethod.GET)) {
				return listMethodTestTemplate;
			}
			if (method.equals(HttpMethod.POST)) {
				return createMethodTestTemplate;
			}
		} else {
			if (method.equals(HttpMethod.GET)) {
				return readMethodTestTemplate;
			}
			if (method.equals(HttpMethod.PUT)) {
				return updateMethodTestTemplate;
			}
			if (method.equals(HttpMethod.DELETE)) {
				return deleteMethodTestTemplate;
			}
		}
		return null;
	}
}
