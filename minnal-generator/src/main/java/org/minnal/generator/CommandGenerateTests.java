/**
 * 
 */
package org.minnal.generator;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.base.Joiner;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author ganeshs
 *
 */
@Parameters(separators = "=", commandDescription = "Generates the resource tests")
public class CommandGenerateTests extends ExecutableCommand {
	
	@Parameter(names = "-projectDir", description = "The project directory")
	private String projectDir = System.getProperty("user.dir");
	
	@Parameter(names = "-packages", description = "The list of packages", variableArity=true)
	private List<String> packages = new ArrayList<String>();
	
	@Override
	public void execute() {
		execute(Arrays.asList(new String[]{"mvn", "-X", "-f", projectDir + "/pom.xml", "compile", "exec:exec", "-Dexec.executable=java", "-DmainClass=org.minnal.generator.test.TestsGenerator", "-Dexec.args=-cp %classpath org.minnal.generator.test.TestsGenerator " + projectDir + " " + Joiner.on(",").join(packages)}));
	}

	/**
	 * @return the projectDir
	 */
	public String getProjectDir() {
		return projectDir;
	}

	/**
	 * @param projectDir the projectDir to set
	 */
	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}
}
