/**
 * 
 */
package org.minnal.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
		verify(application).addExceptionMapper(ConstraintViolationExceptionHandler.class);
	}
}
