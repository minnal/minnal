package org.minnal.jaxrs.test;

import org.minnal.instrument.entity.AggregateRootScanner;
import org.minnal.jaxrs.test.core.AbstractGenerator;
import org.minnal.utils.scanner.Scanner;
import org.minnal.utils.scanner.Scanner.Listener;

import java.io.File;
import java.util.Arrays;

public class TestsGenerator extends AbstractGenerator {
	
	private String projectDir;
	
	private String[] packages;
	
	public TestsGenerator(String projectDir, String[] packages) {
		super(new File(projectDir));
		this.packages = Arrays.copyOf(packages, packages.length);
		this.projectDir = projectDir;
	}
	
	@Override
	public void init() {
		Scanner<Class<?>> scanner = new AggregateRootScanner(packages);
		scanner.scan(new Listener<Class<?>>() {
			public void handle(Class<?> entityClass) {
				addGenerator(new ResourceClassTestGenerator(projectDir, entityClass));
			}
		});
	}
	
	public static void main(String[] args) {
		String projectDir = args.length > 0 ? args[0] : System.getProperty("user.dir");
		String[] packages = args.length > 1 ? args[1].split(",") : new String[]{};
		TestsGenerator generator = new TestsGenerator(projectDir, packages);
		generator.init();
		generator.generate();
	}
}
