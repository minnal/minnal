/**
 * 
 */
package org.minnal.generator;

import com.google.common.base.Charsets;
import org.minnal.generator.exception.MinnalGeneratorException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * @author ganeshs
 *
 */
public abstract class ExecutableCommand implements Command {

	public void execute(List<String> commandArgs) {
		ProcessBuilder builder = new ProcessBuilder(commandArgs);
		try {
			Process p = builder.start();
			inheritIO(p.getInputStream(), System.out);
			inheritIO(p.getErrorStream(), System.err);
		} catch (Exception e) {
			throw new MinnalGeneratorException("Failed while starting the project", e);
		}
	}
	
	private void inheritIO(final InputStream src, final PrintStream dest) {
	    new Thread(new Runnable() {
	        public void run() {
	            Scanner sc = new Scanner(src, Charsets.UTF_8.name());
	            while (sc.hasNextLine()) {
	                dest.println(sc.nextLine());
	            }
	            sc.close();
	        }
	    }).start();
	}
}
