/**
 * 
 */
package org.minnal.generator;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class MinnalGenerator {
	
	private CommandAdd commandAdd = new CommandAdd();
	
	private CommandNew commandNew = new CommandNew();
	
	private Args args = new Args();
	
	private CommandStart commandStart = new CommandStart();
	
	private JCommander jc = new JCommander(args);
	
	private static final Logger logger = LoggerFactory.getLogger(MinnalGenerator.class);
	
	public MinnalGenerator() {
		jc.setProgramName("minnal");
		jc.addCommand("new", commandNew);
		jc.addCommand("add", commandAdd);
		jc.addCommand("start", commandStart);
	}
	
	protected void process(String[] args) {
		try {
			logger.trace("Parsing the args {}", (Object) args);
			jc.parse(args);
		} catch (ParameterException e) {
			logger.debug("Failed while parsing the args " + args, e);
			jc.usage();
			return;
		}
		if (this.args.isTrace()) {
			LogManager.getRootLogger().setLevel(Level.TRACE);
		} else if (this.args.isDebug()) {
			LogManager.getRootLogger().setLevel(Level.DEBUG);
		}
			
		String command = jc.getParsedCommand();
		if (Strings.isNullOrEmpty(command)) {
			logger.debug("Command is missing. Please specify a command");
			jc.usage();
			return;
		}
		
		if (this.args.isHelp()) {
			if (command != null) {
				jc.usage(command);
			} else {
				jc.usage();
			}
			return;
		}
		
		if (command.equalsIgnoreCase("new")) {
			logger.debug("Running command new");
			run(commandNew);
		} else if (command.equalsIgnoreCase("add")) {
			logger.debug("Running command add");
			run(commandAdd);
		} if (command.equalsIgnoreCase("start")) {
			logger.debug("Running command start");
			run(commandStart);
		}
	}
	
	protected void run(Command command) {
		try {
			command.execute();
		} catch (MinnalException e) {
			if (! logger.isDebugEnabled()) {
				logger.error(e.getMessage());
			}
			logger.debug(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		MinnalGenerator generator = new MinnalGenerator();
		generator.process(args);
	}
}
