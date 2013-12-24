/**
 * 
 */
package org.minnal.generator;

import java.util.ArrayList;
import java.util.List;

import org.minnal.generator.core.Generator;
import org.minnal.generator.core.ProjectGenerator;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author ganeshs
 * 
 */
@Parameters(separators = "=", commandDescription = "Create a new minnal project")
public class CommandNew implements Command {

	@Parameter(description = "The name of the project to create", required=true)
	private List<String> values = new ArrayList<String>();

	@Parameter(names = "-nojpa", description = "Exclude Jpa plugin")
	private boolean nojpa;

	@Parameter(names = "-noinst", description = "Exclude instrumenation bundle")
	private boolean noinst;
	
	@Parameter(names = "-noadmin", description = "Exclude admin application")
	private boolean noadmin;
	
	@Parameter(names = "-basedir", description = "The dir under which the project has to be created")
	private String baseDir = System.getProperty("user.dir");
	
	@Parameter(names = "-version", description = "The minnal version to use")
	private String version = "1.1.2";

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return values.get(0);
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		values.add(projectName);
	}

	/**
	 * @return the nojpa
	 */
	public boolean isNojpa() {
		return nojpa;
	}

	/**
	 * @param nojpa the nojpa to set
	 */
	public void setNojpa(boolean nojpa) {
		this.nojpa = nojpa;
	}

	/**
	 * @return the noinst
	 */
	public boolean isNoinst() {
		return noinst;
	}

	/**
	 * @param noinst the noinst to set
	 */
	public void setNoinst(boolean noinst) {
		this.noinst = noinst;
	}

	/**
	 * @return the baseDir
	 */
	public String getBaseDir() {
		return baseDir;
	}

	/**
	 * @param baseDir the baseDir to set
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the noadmin
	 */
	public boolean isNoadmin() {
		return noadmin;
	}

	/**
	 * @param noadmin the noadmin to set
	 */
	public void setNoadmin(boolean noadmin) {
		this.noadmin = noadmin;
	}
	
	@Override
	public void execute() {
		Generator generator = new ProjectGenerator(this);
		generator.init();
		generator.generate();
	}
}
