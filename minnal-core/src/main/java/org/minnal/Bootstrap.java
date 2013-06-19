/**
 * 
 */
package org.minnal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Bootstrap is the entry point to minnal and is the main class. The main method loads the container, initializes and starts the container. 
 * It also registers a shutdown hook for gracefully shutting down the container. To stop the minnal server, use SIGTERM the running process</p> 
 *  
 * @author ganeshs
 *
 */
public class Bootstrap {
	
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	public static void main(String[] args) {
		try {
			final Class<?> containerClass = Thread.currentThread().getContextClassLoader().loadClass("org.minnal.core.Container");
			final Object container = args != null && args.length > 0 ? 
					containerClass.getConstructor(String.class).newInstance(args[0]) : 
					containerClass.newInstance();

			containerClass.getMethod("init").invoke(container);
			containerClass.getMethod("start").invoke(container);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						logger.info("Shutdown hook triggered. Stopping the container");
						containerClass.getMethod("stop").invoke(container);
					} catch (Exception e) {
						logger.error("Failed while stopping the container", e);
					}
				}
			});
		} catch (Exception e) {
			logger.error("Failed while starting the container", e);
			System.exit(1);
		}
	}
}
