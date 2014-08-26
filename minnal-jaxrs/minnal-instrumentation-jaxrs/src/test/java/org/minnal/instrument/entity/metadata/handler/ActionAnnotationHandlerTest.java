/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.NonAggregateRootModel;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ActionAnnotationHandlerTest {
	
	private Action annotation;
	
	private EntityMetaData metaData;
	
	private ActionAnnotationHandler handler;

	@BeforeMethod
	public void setup() {
		handler = new ActionAnnotationHandler();
		metaData = mock(EntityMetaData.class);
		annotation = mock(Action.class);
		when(metaData.getEntityClass()).thenReturn((Class)DummyModel.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), Action.class);
	}
	
	@Test
	public void shouldAddActionMethodToMetadataWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("dummyAction");
		handler.handle(metaData, annotation, method);
		ActionMetaData data = new ActionMetaData("dummyAction", "/dummy", method);
		verify(metaData).addActionMethod(data);
	}
	
	@Test
	public void shouldAddActionMethodWithCustomValueToMetadataWhenOnMethod() throws Exception {
		when(annotation.value()).thenReturn("customAction");
		Method method = DummyModel.class.getDeclaredMethod("dummyAction");
		handler.handle(metaData, annotation, method);
		ActionMetaData data = new ActionMetaData("customAction", "/custom", method);
		verify(metaData).addActionMethod(data);
	}
	
	@Test
	public void shouldAddActionMethodToMetadataWithParamsWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("dummyAction", String.class, Long.class);
		handler.handle(metaData, annotation, method);
		ActionMetaData data = new ActionMetaData("dummyAction", "/dummy", method);
		data.addParameter(new ParameterMetaData("param1", "param1", String.class));
		data.addParameter(new ParameterMetaData("param2", "param2", Long.class));
		verify(metaData).addActionMethod(data);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAddActionToMetadataWhenOnField() throws Exception {
		Field field  = DummyModel.class.getDeclaredField("children");
		handler.handle(metaData, annotation, field);
	}
	
	@Test(expectedExceptions=MinnalInstrumentationException.class)
	public void shouldThrowExceptionWhenActionSpecifiedOnNonAggregateRoot() throws Exception {
		when(metaData.getEntityClass()).thenReturn((Class) NonAggregateRootModel.class);
		Method method = NonAggregateRootModel.class.getDeclaredMethod("dummyAction");
		handler.handle(metaData, annotation, method);
	}
}
