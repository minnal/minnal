/**
 * 
 */
package org.minnal.generator;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author ganeshs
 *
 */
@Parameters(separators = "=", commandDescription = "Start the minnal project")
public class CommandStart extends ExecutableCommand {
	
	@Parameter(description="The minnal project directory")
	private List<String> values = new ArrayList<String>();
	
	@Parameter(names = "-projectDir", description = "The project directory")
	private String projectDir = System.getProperty("user.dir");

	@Override
	public void execute() {
		if (values.size() > 0) {
			projectDir = values.get(0);
		}
		execute(Arrays.asList(new String[]{"mvn", "-X", "-f", projectDir + "/pom.xml", "compile", "exec:exec", "-DmainClass=org.minnal.Bootstrap"}));
	}
}
