/**
 * 
 */
package org.minnal.generator.test;

import java.io.File;

import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.scanner.Scanner;
import org.minnal.core.scanner.Scanner.Listener;
import org.minnal.generator.core.AbstractGenerator;
import org.minnal.instrument.entity.AggregateRootScanner;

/**
 * @author ganeshs
 *
 */
public class TestsGenerator extends AbstractGenerator {
	
	private String projectDir;
	
	private String[] packages;
	
	public TestsGenerator(String projectDir, String[] packages) {
		super(new File(projectDir));
		this.packages = packages;
		this.projectDir = projectDir;
	}
	
	@Override
	public void init() {
		Scanner<Class<?>> scanner = new AggregateRootScanner(packages);
		scanner.scan(new Listener<Class<?>>() {
			public void handle(Class<?> t) {
				ResourceClass resourceClass = new ResourceClass(t, new ResourceConfiguration(""));
				addGenerator(new ResourceClassTestGenerator(projectDir, resourceClass));
			}
		});
	}
	
	public static void main(String[] args) {
		String projectDir = args.length > 0 ? args[0] : System.getProperty("user.dir");
		String[] packages = args.length > 1 ? args[1].split(",") : new String[]{};
//		projectDir = "/Volumes/data/ERP/services/ekl-facilities";
//		packages = new String[]{"com.ekl"};
		
		TestsGenerator generator = new TestsGenerator(projectDir, packages);
		generator.init();
		generator.generate();
	}
}
