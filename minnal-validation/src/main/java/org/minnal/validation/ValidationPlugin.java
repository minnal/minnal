/**
 * 
 */
package org.minnal.validation;

import org.minnal.core.Application;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.validation.exception.ConstraintViolationExceptionHandler;

/**
 * @author ganeshs
 *
 */
public class ValidationPlugin implements Plugin {

	@Override
	public void init(Application<? extends ApplicationConfiguration> application) {
		application.addExceptionMapper(ConstraintViolationExceptionHandler.class);
	}

	@Override
	public void destroy() {
	}

}
