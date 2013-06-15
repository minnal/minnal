/**
 * 
 */
package org.minnal.generator;

import com.beust.jcommander.Parameter;


/**
 * @author ganeshs
 *
 */
public class Args {

	@Parameter(names="-help", help=true)
	private boolean help;
	
	@Parameter(names="-debug")
	private boolean debug;
	
	@Parameter(names="-trace")
	private boolean trace;

	/**
	 * @return the help
	 */
	public boolean isHelp() {
		return help;
	}

	/**
	 * @param help the help to set
	 */
	public void setHelp(boolean help) {
		this.help = help;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @return the trace
	 */
	public boolean isTrace() {
		return trace;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
}
