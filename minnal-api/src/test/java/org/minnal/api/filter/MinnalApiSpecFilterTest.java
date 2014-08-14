/**
 * 
 */
package org.minnal.api.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import scala.Option;

import com.wordnik.swagger.model.Parameter;

/**
 * @author ganeshs
 *
 */
public class MinnalApiSpecFilterTest {

	@Test
	public void shouldReturnFalseIfParamAccessIsInternal() {
		MinnalApiSpecFilter filter = spy(new MinnalApiSpecFilter());
		Parameter parameter = mock(Parameter.class);
		when(parameter.paramAccess()).thenReturn(Option.apply("internal"));
		assertFalse(filter.isParamAllowed(parameter, null, null, null, null, null));
	}
	
	@Test
	public void shouldReturnTrueIfParamAccessIsNotInternal() {
		MinnalApiSpecFilter filter = spy(new MinnalApiSpecFilter());
		Parameter parameter = mock(Parameter.class);
		when(parameter.paramAccess()).thenReturn(Option.apply("internal1"));
		assertTrue(filter.isParamAllowed(parameter, null, null, null, null, null));
	}
	
	@Test
	public void shouldReturnTrueIfParamAccessIsNotSet() {
		MinnalApiSpecFilter filter = spy(new MinnalApiSpecFilter());
		Parameter parameter = mock(Parameter.class);
		when(parameter.paramAccess()).thenReturn(Option.<String>empty());
		assertTrue(filter.isParamAllowed(parameter, null, null, null, null, null));
	}
}
