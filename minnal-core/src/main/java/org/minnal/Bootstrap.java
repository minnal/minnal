/**
 * 
 */
package org.minnal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class Bootstrap {
	
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		try {
			Class<?> containerClass = Thread.currentThread().getContextClassLoader().loadClass("org.minnal.core.Container");
			Object container = null;
			if (args != null && args.length > 0) {
				container = containerClass.getConstructor(String.class).newInstance(args[0]);
			} else {
				container = containerClass.newInstance();
			}
			containerClass.getMethod("init").invoke(container);
			containerClass.getMethod("start").invoke(container);
		} catch (Exception e) {
			logger.error("Failed while starting the container", e);
			System.exit(1);
		}
	}
}
