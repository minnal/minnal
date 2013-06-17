/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;

import org.minnal.core.MinnalException;
import org.minnal.generator.CommandNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ProjectGenerator extends AbstractGenerator {
	
	private CommandNew command;
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectGenerator.class);
	
	public ProjectGenerator(CommandNew command) {
		super(createProjectDir(command));
		this.command = command;
	}
	
	@Override
	public void init() {
		super.init();
		createFolder(MAIN_JAVA_FOLDER);
		createFolder(TEST_JAVA_FOLDER);
		createFolder(MAIN_RESOURCES_FOLDER);
		createFolder(TEST_RESOURCES_FOLDER);
		createFolder(MAIN_META_INF_FOLDER);
		createFolder(MAIN_SERVICES_FOLDER);
		
		PomGenerator pomGenerator = createPomGenerator();
		ApplicationGenerator applicationGenerator = new ApplicationGenerator(baseDir, !command.isNojpa());
		ContainerConfigGenerator containerConfigGenerator = new ContainerConfigGenerator(baseDir);
		containerConfigGenerator.addApplication(getApplicationClassName(), "/");
		if (! command.isNoadmin()) {
			containerConfigGenerator.addApplication("org.minnal.admin.AdminApplication", "/admin");
		}
		
		addGenerator(pomGenerator);
		addGenerator(containerConfigGenerator);
		addGenerator(applicationGenerator);
		addGenerator(new LoggerConfigGenerator(baseDir));
	}
	
	private PomGenerator createPomGenerator() {
		PomGenerator pomGenerator = new PomGenerator(baseDir, command.getProjectName());
		pomGenerator.addDependency("org.minnal", "minnal-core", command.getVersion());
		pomGenerator.addDependency("org.hibernate", "hibernate-entitymanager", "4.2.1.Final");
		pomGenerator.addDependency("org.hsqldb", "hsqldb", "2.2.9");
		pomGenerator.addDependency("org.testng", "testng", "6.8.1", "test");
		pomGenerator.addDependency("org.minnal", "minnal-test", command.getVersion(), "test");
		pomGenerator.addDependency("org.minnal", "minnal-migrations", command.getVersion());
		pomGenerator.addDependency("org.mockito", "mockito-all", "1.9.5");
		
		if (! command.isNojpa()) {
			pomGenerator.addDependency("org.minnal", "minnal-jpa", command.getVersion());
		}
		if (! command.isNoinst()) {
			pomGenerator.addDependency("org.minnal", "minnal-instrumentation", command.getVersion());
		}
		if (! command.isNoinst()) {
			pomGenerator.addDependency("org.minnal", "minnal-admin", command.getVersion());
		}
		return pomGenerator;
	}

	protected static File createProjectDir(CommandNew command) {
		logger.info("Creating the project {} under {}", command.getProjectName(), command.getBaseDir());
		File dir = new File(command.getBaseDir());
		if (! dir.exists()) {
			logger.trace("Directory {} doesn't exist. Creating one.", command.getBaseDir());
			dir.mkdirs();
		} else if (! dir.isDirectory()) {
			logger.trace("Path {} is not a directory", command.getBaseDir());
			throw new MinnalException("Path " + command.getBaseDir() + " is not a directory");
		}
		File projectDir = new File(dir, command.getProjectName());
		if (projectDir.exists()) {
			logger.trace("Project directory {} already exists", projectDir.getAbsolutePath());
			throw new MinnalException("Project dir " + projectDir.getAbsolutePath() + " already exists");
		}
		projectDir.mkdirs();
		return projectDir;
	}
	
}