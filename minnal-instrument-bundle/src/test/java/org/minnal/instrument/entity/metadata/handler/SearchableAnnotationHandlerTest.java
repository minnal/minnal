/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.Searchable;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SearchableAnnotationHandlerTest {

	private Searchable annotation;
	
	private EntityMetaData metaData;
	
	private SearchableAnnotationHandler handler;

	@BeforeMethod
	public void setup() {
		handler = new SearchableAnnotationHandler();
		metaData = mock(EntityMetaData.class);
		annotation = mock(Searchable.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), Searchable.class);
	}
	
	@Test
	public void shouldAddSearchFieldToMetadataWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getCode");
		handler.handle(metaData, annotation, method);
		ParameterMetaData data = new ParameterMetaData("code", "code", String.class);
		metaData.addSearchField(data);
	}
	
	@Test
	public void shouldAddSearchFieldWithValueToMetadataWhenOnMethod() throws Exception {
		when(annotation.value()).thenReturn("customCode");
		Method method = DummyModel.class.getDeclaredMethod("getCode");
		handler.handle(metaData, annotation, method);
		ParameterMetaData data = new ParameterMetaData("customCode", "code", String.class);
		metaData.addSearchField(data);
	}
	
	@Test
	public void shouldAddSearchFieldToMetadataWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("code");
		handler.handle(metaData, annotation, field);
		ParameterMetaData data = new ParameterMetaData("code", "code", String.class);
		metaData.addSearchField(data);
	}
	
	@Test
	public void shouldAddSearchFieldWithValueToMetadataWhenOnField() throws Exception {
		when(annotation.value()).thenReturn("customCode");
		Field field = DummyModel.class.getDeclaredField("code");
		handler.handle(metaData, annotation, field);
		ParameterMetaData data = new ParameterMetaData("customCode", "code", String.class);
		metaData.addSearchField(data);
	}
}
