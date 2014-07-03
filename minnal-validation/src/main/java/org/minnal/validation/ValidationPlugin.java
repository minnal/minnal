/**
 * 
 */
package org.minnal.validation;

import javax.validation.ConstraintViolationException;

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
		application.addExceptionHandler(ConstraintViolationException.class, new ConstraintViolationExceptionHandler());
	}

	@Override
	public void destroy() {
	}

}
