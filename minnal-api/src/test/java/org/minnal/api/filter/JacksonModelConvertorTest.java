/**
 * 
 */
package org.minnal.api.filter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Timestamp;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import scala.Option;
import scala.collection.immutable.Map;
import scala.collection.mutable.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.collect.Lists;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;

/**
 * @author ganeshs
 *
 */
public class JacksonModelConvertorTest {

	private JacksonModelConvertor convertor;
	
	private Model model;
	
	private LinkedHashMap<String, ModelProperty> properties;
	
	@BeforeMethod
	public void setup() {
		convertor = spy(new JacksonModelConvertor(Lists.<Class<? extends Annotation>>newArrayList(JsonBackReference.class)));
		model = mock(Model.class);
		properties = mock(LinkedHashMap.class);
		when(model.properties()).thenReturn(properties);
	}
	
	@Test
	public void shouldReadFieldsAndHandleThem() throws NoSuchFieldException, SecurityException {
		doNothing().when(convertor).handleExcludedAnnotations(eq(DummyModel.class), any(Field.class), any(Option.class));
		doNothing().when(convertor).handleTimestampField(any(Field.class), any(Option.class));
		convertor.read(DummyModel.class, mock(Map.class));
		verify(convertor).handleExcludedAnnotations(eq(DummyModel.class), eq(DummyModel.class.getDeclaredField("field1")), any(Option.class));
		verify(convertor).handleExcludedAnnotations(eq(DummyModel.class), eq(DummyModel.class.getDeclaredField("field2")), any(Option.class));
		verify(convertor).handleExcludedAnnotations(eq(DummyModel.class), eq(DummyModel.class.getDeclaredField("field3")), any(Option.class));
		verify(convertor).handleTimestampField(eq(DummyModel.class.getDeclaredField("timestamp")), any(Option.class));
	}
	
	@Test
	public void shouldHandleTimestampType() throws NoSuchFieldException, SecurityException {
		properties = new LinkedHashMap<String, ModelProperty>();
		ModelProperty property = new ModelProperty("java.sql.Timestamp", "java.sql.Timestamp", 1, true, Option.<String>empty(), null, null);
		properties.put("timestamp", property);
		when(model.properties()).thenReturn(properties);
		convertor.handleTimestampField(DummyModel.class.getDeclaredField("timestamp"), Option.apply(model));
		assertEquals(properties.get("timestamp").get().type(), "date-time");
	}
	
	@Test
	public void shouldNotHandleNonTimestampType() throws NoSuchFieldException, SecurityException {
		properties = new LinkedHashMap<String, ModelProperty>();
		ModelProperty property = new ModelProperty("string", "string", 1, true, Option.<String>empty(), null, null);
		properties.put("field1", property);
		when(model.properties()).thenReturn(properties);
		convertor.handleTimestampField(DummyModel.class.getDeclaredField("field1"), Option.apply(model));
		assertEquals(properties.get("field1").get().type(), "string");
	}

	@Test
	public void shouldRemoveExcludedFieldAnnotationsFromModel() throws NoSuchFieldException, SecurityException {
		convertor.handleExcludedAnnotations(DummyModel.class, DummyModel.class.getDeclaredField("field1"), Option.apply(model));
		verify(properties).remove("field1");
	}
	
	@Test
	public void shouldRemoveExcludedMethodAnnotationsFromModel() throws NoSuchFieldException, SecurityException {
		convertor.handleExcludedAnnotations(DummyModel.class, DummyModel.class.getDeclaredField("field2"), Option.apply(model));
		verify(properties).remove("field2");
	}
	
	@Test
	public void shouldNotRemoveFieldsWithoutAnnotations() throws NoSuchFieldException, SecurityException {
		convertor.handleExcludedAnnotations(DummyModel.class, DummyModel.class.getDeclaredField("field3"), Option.apply(model));
		verify(properties, never()).remove("field3");
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	private static class DummyModel {
		
		@JsonBackReference
		private String field1;
		
		private String field2;
		
		private String field3;
		
		private Timestamp timestamp;

		/**
		 * @return the field1
		 */
		public String getField1() {
			return field1;
		}

		/**
		 * @param field1 the field1 to set
		 */
		public void setField1(String field1) {
			this.field1 = field1;
		}

		/**
		 * @return the field2
		 */
		@JsonBackReference
		public String getField2() {
			return field2;
		}

		/**
		 * @param field2 the field2 to set
		 */
		public void setField2(String field2) {
			this.field2 = field2;
		}

		/**
		 * @return the field3
		 */
		public String getField3() {
			return field3;
		}

		/**
		 * @param field3 the field3 to set
		 */
		public void setField3(String field3) {
			this.field3 = field3;
		}
	}
}
