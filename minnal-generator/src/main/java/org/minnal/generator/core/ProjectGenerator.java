/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.javalite.common.Inflector;
import org.minnal.core.MinnalException;
import org.minnal.generator.CommandNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ProjectGenerator implements Generator {
	
	private CommandNew command;
	
	private List<Generator> generators = new ArrayList<Generator>();
	
	private static final Logger logger = LoggerFactory.getLogger(ProjectGenerator.class);
	
	public ProjectGenerator(CommandNew command) {
		this.command = command;
		init();
	}
	
	protected void init() {
		File projectDir = createProjectDir();
		File mainSrc = createFolder(projectDir, "src/main/java");
		File testSrc = createFolder(projectDir, "src/test/java");
		File mainResources = createFolder(projectDir, "src/main/resources");
		File testResources = createFolder(projectDir, "src/test/resources");
		
		addPomGenerator(projectDir);
		String applicationName = command.getProjectName().toLowerCase().replace('-', '_');
		ApplicationGenerator applicationGenerator = new ApplicationGenerator(mainSrc.getAbsolutePath(), 
				mainResources.getAbsolutePath(), Inflector.camelize(applicationName, false), "com." + applicationName.replace('_', '.'));
		ContainerConfigGenerator containerConfigGenerator = new ContainerConfigGenerator(mainResources.getAbsolutePath());
		containerConfigGenerator.addApplication(applicationGenerator.getPackageName() + "." + applicationGenerator.getApplicationClassName(), "/");
		if (! command.isNoadmin()) {
			containerConfigGenerator.addApplication("org.minnal.admin.AdminApplication", "/admin");
		}
		generators.add(containerConfigGenerator);
		generators.add(applicationGenerator);
	}
	
	private void addPomGenerator(File projectDir) {
		PomGenerator pomGenerator = new PomGenerator(projectDir.getAbsolutePath(), command.getProjectName());
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
		
		generators.add(pomGenerator);
	}

	public void generate() {
		for (Generator generator : generators) {
			generator.generate();
		}
	}
	
	protected File createProjectDir() {
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
	
	private File createFolder(File basedir, String folderName) {
		logger.info("Creating the folder {} under {}", folderName, basedir);
		File folder = new File(basedir, folderName);
		folder.mkdirs();
		return folder;
	}
}
