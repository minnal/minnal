/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;

import org.activejpa.entity.Condition;
import org.activejpa.entity.Filter;
import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class ResourceUtilTest {

	@Test
	public void shouldCreateFilter() {
		MultivaluedMap<String, String> map = mock(MultivaluedMap.class);
		when(map.getFirst(ResourceUtil.PAGE_NO)).thenReturn("1");
		when(map.getFirst(ResourceUtil.PER_PAGE)).thenReturn("10");
		assertEquals(ResourceUtil.getFilter(map), new Filter(10, 1));
	}
	
	@Test
	public void shouldCreateFilterWithSearchParams() {
		MultivaluedMap<String, String> map = mock(MultivaluedMap.class);
		when(map.getFirst(ResourceUtil.PAGE_NO)).thenReturn("1");
		when(map.getFirst(ResourceUtil.PER_PAGE)).thenReturn("10");
		when(map.getFirst("param1")).thenReturn("value1");
		when(map.getFirst("param2")).thenReturn("value2");
		List<String> params = Arrays.asList("param1", "param2");
		assertEquals(ResourceUtil.getFilter(map, params), new Filter(10, 1, new Condition("param1", "value1"), new Condition("param2", "value2")));
	}
	
	@Test
	public void shouldCreateFilterWithUnderscoreSeparatedSearchParams() {
		MultivaluedMap<String, String> map = mock(MultivaluedMap.class);
		when(map.getFirst(ResourceUtil.PAGE_NO)).thenReturn("1");
		when(map.getFirst(ResourceUtil.PER_PAGE)).thenReturn("10");
		when(map.getFirst("param_name1")).thenReturn("value1");
		when(map.getFirst("param_name2")).thenReturn("value2");
		List<String> params = Arrays.asList("param_name1", "param_name2");
		assertEquals(ResourceUtil.getFilter(map, params), new Filter(10, 1, new Condition("paramName1", "value1"), new Condition("paramName2", "value2")));
	}
	
	@Test
	public void shouldGetContentAsList() throws Exception {
		List<String> list = Lists.newArrayList("test1", "test2", "test3");
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(String.class), eq(String.class), eq(new Annotation[]{}), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenThrow(IOException.class);
		when(reader.readFrom(eq(List.class), any(Type.class), eq(new Annotation[]{}), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(list);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(String.class, String.class, new Annotation[]{}, mediaType)).thenReturn(reader);
		when(providers.getMessageBodyReader(List.class, ResourceUtil.listType(String.class).getType(), new Annotation[]{}, mediaType)).thenReturn(reader);
		Object content = ResourceUtil.getContent(bytes, providers, httpHeaders, String.class);
		assertTrue(content instanceof List);
		assertEquals(content, list);
	}
	
	@Test
	public void shouldGetContentAsMap() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "value1");
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(Map.class), eq(Map.class), eq(new Annotation[]{}), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(map);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(Map.class, Map.class, new Annotation[]{}, mediaType)).thenReturn(reader);
		Object content = ResourceUtil.getContent(bytes, providers, httpHeaders, Map.class);
		assertTrue(content instanceof Map);
		assertEquals(content, map);
	}
	
	@Test
	public void shouldCheckIfStringIsCommaSeparated() {
		assertTrue(ResourceUtil.isCommaSeparated("test,test1"));
		assertFalse(ResourceUtil.isCommaSeparated("testtest1"));
	}
	
	@Test
	public void shouldGetCommaSeparatedValues() {
		assertEquals(ResourceUtil.getCommaSeparatedValues("test,test1"), new String[]{"test", "test1"});
		assertEquals(ResourceUtil.getCommaSeparatedValues("testtest1"), new String[]{"testtest1"});
	}
	
	@Test
	public void shouldInvokeMethodWithNoArguments() throws Throwable {
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(Map.class), eq(Map.class), isNull(Annotation[].class), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(values);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(Map.class, Map.class, null, mediaType)).thenReturn(reader);
		
		assertEquals(ResourceUtil.invokeAction(model, "dummyAction", new ArrayList<ParameterMetaData>(), bytes, providers, httpHeaders, values), "dummy");
	}
	
	@Test
	public void shouldInvokeMethodWithArguments() throws Throwable {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("value", "test123");
		content.put("someNumber", 1L);
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(Map.class), eq(Map.class), isNull(Annotation[].class), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(content);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(Map.class, Map.class, null, mediaType)).thenReturn(reader);

		assertEquals(ResourceUtil.invokeAction(model, "dummyActionWithArguments", Lists.newArrayList(new ParameterMetaData("value", "value", String.class), 
				new ParameterMetaData("someNumber", "someNumber", Long.class)), bytes, providers, httpHeaders, values), "dummyActionWithArguments");
	}
	
	@Test
	public void shouldInvokeMethodWithArgumentsAndModel() throws Throwable {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("value", "test123");
		content.put("someNumber", 1L);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("anotherModel", new DummyModel());
		DummyModel model = new DummyModel();
		
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(Map.class), eq(Map.class), isNull(Annotation[].class), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(content);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(Map.class, Map.class, null, mediaType)).thenReturn(reader);
		
		assertEquals(ResourceUtil.invokeAction(model, "dummyActionWithArgumentsAndModel", Lists.newArrayList(new ParameterMetaData("anotherModel", "anotherModel", DummyModel.class),
				new ParameterMetaData("value", "value", String.class), new ParameterMetaData("someNumber", "someNumber", Long.class)), bytes, providers, httpHeaders, values), "dummyActionWithArgumentsAndModel");
	}
	
	@Test(expectedExceptions=MinnalInstrumentationException.class)
	public void shouldThrowExceptionIfMethodNotFound() throws Throwable {
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(Map.class), eq(Map.class), isNull(Annotation[].class), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(values);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(Map.class, Map.class, null, mediaType)).thenReturn(reader);
		
		ResourceUtil.invokeAction(model, "nonExistingMethod", Lists.newArrayList(new ParameterMetaData("anotherModel", "anotherModel", DummyModel.class)), bytes, providers, httpHeaders, values);
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldThrowExceptionIfMethodThrowsAnyException() throws Throwable {
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		
		byte[] bytes = new byte[10];
		MediaType mediaType = mock(MediaType.class);
		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);
		MessageBodyReader reader = mock(MessageBodyReader.class);
		when(reader.readFrom(eq(Map.class), eq(Map.class), isNull(Annotation[].class), eq(mediaType), isNull(MultivaluedMap.class), any(InputStream.class))).thenReturn(values);
		Providers providers = mock(Providers.class);
		when(providers.getMessageBodyReader(Map.class, Map.class, null, mediaType)).thenReturn(reader);
		
		ResourceUtil.invokeAction(model, "throwsException", new ArrayList<ParameterMetaData>(), bytes, providers, httpHeaders, values);
	}
	
	public static class DummyModel {
		
		public String dummyAction() {
			return "dummy";
		}
		
		public String dummyActionWithArguments(String value, Long someNumber) {
			if (value == null || someNumber == null) {
				throw new IllegalArgumentException();
			}
			return "dummyActionWithArguments";
		}
		
		public String dummyActionWithArgumentsAndModel(DummyModel anotherModel, String value, Long someNumber) {
			if (value == null || someNumber == null || anotherModel == null) {
				throw new IllegalArgumentException();
			}
			return "dummyActionWithArgumentsAndModel";
		}
		
		public void throwsException() {
			throw new IllegalStateException();
		}
	}
}
