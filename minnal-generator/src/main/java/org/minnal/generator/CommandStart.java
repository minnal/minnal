/**
 * 
 */
package org.minnal.generator;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.minnal.core.MinnalException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author ganeshs
 *
 */
@Parameters(separators = "=", commandDescription = "Start the minnal project")
public class CommandStart implements Command {
	
	@Parameter(description="The minnal project directory")
	private List<String> values = new ArrayList<String>();

	@Override
	public void execute() {
		String projectDir = System.getProperty("user.dir");
		if (values.size() > 0) {
			projectDir = values.get(0);
		}
		ProcessBuilder builder = new ProcessBuilder(Arrays.asList(new String[]{"mvn", "-f", projectDir + "/pom.xml", "compile", "exec:exec"}));
		try {
			Process p = builder.start();
			inheritIO(p.getInputStream(), System.out);
			inheritIO(p.getErrorStream(), System.err);
		} catch (Exception e) {
			throw new MinnalException("Failed while starting the project", e);
		}
	}
	
	private static void inheritIO(final InputStream src, final PrintStream dest) {
	    new Thread(new Runnable() {
	        public void run() {
	            Scanner sc = new Scanner(src);
	            while (sc.hasNextLine()) {
	                dest.println(sc.nextLine());
	            }
	        }
	    }).start();
	}

}
