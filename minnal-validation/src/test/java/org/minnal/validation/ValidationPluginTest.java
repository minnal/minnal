/**
 * 
 */
package org.minnal.validation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.validation.ConstraintViolationException;

import org.minnal.core.Application;
import org.minnal.validation.exception.ConstraintViolationExceptionHandler;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ValidationPluginTest {

	@Test
	public void shouldAddConstraintViolationExceptionHandler() {
		Application application = mock(Application.class);
		ValidationPlugin plugin = new ValidationPlugin();
		plugin.init(application);
		verify(application).addExceptionHandler(eq(ConstraintViolationException.class), any(ConstraintViolationExceptionHandler.class));
	}
}
