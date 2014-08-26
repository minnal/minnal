/**
 *
 */
package org.minnal.generator.test;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.javalite.common.Inflector;
import org.minnal.generator.core.AbstractTemplateGenerator;
import org.minnal.generator.exception.MinnalGeneratorException;
import org.minnal.generator.util.CodeUtils;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.UnderscoreNamingStrategy;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.utils.Node.PathVisitor;
import org.minnal.utils.route.RoutePattern;

import javax.ws.rs.HttpMethod;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ganeshs
 */
public class ResourceClassTestGenerator extends AbstractTemplateGenerator {

    private String baseTestClass;
    private Class<?> entityClass;

    private List<EntityNodePath> paths = new ArrayList<EntityNode.EntityNodePath>();

    private NamingStrategy namingStrategy = new UnderscoreNamingStrategy();

    private static Template createMethodTestTemplate = engine.getTemplate("META-INF/templates/create_method_test.vm");

    private static Template updateMethodTestTemplate = engine.getTemplate("META-INF/templates/update_method_test.vm");

    private static Template readMethodTestTemplate = engine.getTemplate("META-INF/templates/read_method_test.vm");

    private static Template listMethodTestTemplate = engine.getTemplate("META-INF/templates/list_method_test.vm");

    private static Template deleteMethodTestTemplate = engine.getTemplate("META-INF/templates/delete_method_test.vm");

    private static Template createResourceTestClassTemplate = engine.getTemplate("META-INF/templates/create_resource_class_test.vm");

    /**
     * @param entityClass
     */
    public ResourceClassTestGenerator(String projectDir, Class<?> entityClass, String baseTestClass) {
        super(new File(projectDir));
        this.entityClass = entityClass;
        this.baseTestClass = baseTestClass;
    }

    public void init() {
        EntityNode tree = new EntityNode(entityClass, namingStrategy);
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
        String packageName = entityClass.getPackage().getName() + ".generated";
        String testClass = entityClass.getSimpleName() + "ResourceTest";

        StringBuffer buffer = new StringBuffer();
        for (EntityNodePath path : paths) {
            createMethods(path, buffer);
        }

        VelocityContext context = new VelocityContext();
        context.put("inflector", Inflector.class);
        context.put("package_name", packageName);
        context.put("methods", buffer.toString());
        context.put("resource_class", testClass);
        context.put("base_test_class", baseTestClass);

        StringWriter writer = new StringWriter();
        createResourceTestClassTemplate.merge(context, writer);

        File folder = createPackage(packageName, TEST_JAVA_FOLDER);
        String fileName = testClass + ".java";
        File file = new File(folder, fileName);
        if (file.exists()) {
            File renamedFile = new File(folder, fileName + ".bk");
            if (!file.renameTo(renamedFile)) {
                throw new IllegalStateException("Failed while renaming the file " + file.getPath() + " to " + renamedFile.getPath());
            }
            file = new File(folder, fileName);
        }
        writeFile(CodeUtils.format(writer.toString()), file);
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
            throw new MinnalGeneratorException(e);
        }
    }

    protected StringWriter createMethod(EntityNodePath path, boolean bulk, String method) throws Exception {
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

    protected Template getMethodTemplate(EntityNodePath path, boolean bulk, String method) {
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
