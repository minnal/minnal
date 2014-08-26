/**
 *
 */
package org.minnal.generator.test;

import java.io.File;
import java.util.Arrays;

import org.minnal.generator.core.AbstractGenerator;
import org.minnal.instrument.entity.AggregateRootScanner;
import org.minnal.utils.scanner.Scanner;
import org.minnal.utils.scanner.Scanner.Listener;

/**
 * @author ganeshs
 */
public class TestsGenerator extends AbstractGenerator {

    private String projectDir;

    private String[] packages;

    private String baseTestClass;

    public TestsGenerator(String projectDir, String[] packages, String baseTestClass) {
        super(new File(projectDir));
        this.packages = Arrays.copyOf(packages, packages.length);
        this.projectDir = projectDir;
        this.baseTestClass = baseTestClass;
    }

    @Override
    public void init() {
        Scanner<Class<?>> scanner = new AggregateRootScanner(packages);
        scanner.scan(new Listener<Class<?>>() {
            public void handle(Class<?> entityClass) {
                addGenerator(new ResourceClassTestGenerator(projectDir, entityClass, baseTestClass));
            }
        });
    }

    public static void main(String[] args) {
        String projectDir = args.length > 0 ? args[0] : System.getProperty("user.dir");
        String baseTestClass = args.length > 0 ? args[1] : "org.minnal.test.BaseMinnalResourceTest";
        String[] packages = args.length > 1 ? args[2].split(",") : new String[]{};
        TestsGenerator generator = new TestsGenerator(projectDir, packages, baseTestClass);
        generator.init();
        generator.generate();
    }
}
