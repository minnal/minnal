/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activejpa.entity.Condition;
import org.activejpa.entity.Filter;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.config.RouteConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.serializer.Serializer;
import org.minnal.core.server.ServerRequest;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ResourceUtilTest {

	@Test
	public void shouldCreateFilter() {
		Request request = mock(Request.class);
		when(request.getHeader(ResourceUtil.PAGE_NO)).thenReturn("1");
		when(request.getHeader(ResourceUtil.PER_PAGE)).thenReturn("10");
		assertEquals(ResourceUtil.getFilter(request), new Filter(10, 1));
	}
	
	@Test
	public void shouldCreateFilterWithSearchParams() {
		Request request = mock(Request.class);
		when(request.getHeader(ResourceUtil.PAGE_NO)).thenReturn("1");
		when(request.getHeader(ResourceUtil.PER_PAGE)).thenReturn("10");
		List<String> params = Arrays.asList("param1", "param2");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("param1", "value1");
		headers.put("param2", "value2");
		when(request.getHeaders(params)).thenReturn(headers);
		assertEquals(ResourceUtil.getFilter(request, params), new Filter(10, 1, new Condition("param1", "value1"), new Condition("param2", "value2")));
	}
	
	@Test
	public void shouldCreateFilterWithUnderscoreSeparatedSearchParams() {
		Request request = mock(Request.class);
		when(request.getHeader(ResourceUtil.PAGE_NO)).thenReturn("1");
		when(request.getHeader(ResourceUtil.PER_PAGE)).thenReturn("10");
		List<String> params = Arrays.asList("param_name1", "param_name2");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("param_name1", "value1");
		headers.put("param_name2", "value2");
		when(request.getHeaders(params)).thenReturn(headers);
		assertEquals(ResourceUtil.getFilter(request, params), new Filter(10, 1, new Condition("paramName1", "value1"), new Condition("paramName2", "value2")));
	}
	
	@Test
	public void shouldGetPaginatedResponse() {
		Filter filter = new Filter(100, 3);
		List<?> data = mock(List.class);
		when(data.size()).thenReturn(77);
		Map<String, Object> response = ResourceUtil.getPaginatedResponse(filter, data, 1000L);
		assertEquals(response.get(ResourceUtil.COUNT), data.size());
		assertEquals(response.get(ResourceUtil.PAGE_NO), filter.getPageNo());
		assertEquals(response.get(ResourceUtil.PER_PAGE), filter.getPerPage());
		assertEquals(response.get(ResourceUtil.TOTAL), 1000L);
		assertEquals(response.get(ResourceUtil.DATA), data);
	}
	
	@Test
	public void shouldGetContentAsList() {
		List<String> list = Lists.newArrayList("test1", "test2", "test3");
		HttpRequest request = mock(HttpRequest.class);
		ServerRequest serverRequest = spy(new ServerRequest(request, null));
		when(request.getContent()).thenReturn(Serializer.DEFAULT_JSON_SERIALIZER.serialize(list));
		doReturn(100L).when(serverRequest).getContentLength();
		Route route = mock(Route.class);
		RouteConfiguration configuration = mock(RouteConfiguration.class);
		when(configuration.getSerializer(any(MediaType.class))).thenReturn(Serializer.DEFAULT_JSON_SERIALIZER);
		when(route.getConfiguration()).thenReturn(configuration);
		serverRequest.setResolvedRoute(route);
		Object content = ResourceUtil.getContent(serverRequest, String.class);
		assertTrue(content instanceof List);
		assertEquals(content, list);
	}
	
	@Test
	public void shouldGetContentAsMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "value1");
		HttpRequest request = mock(HttpRequest.class);
		ServerRequest serverRequest = spy(new ServerRequest(request, null));
		when(request.getContent()).thenReturn(Serializer.DEFAULT_JSON_SERIALIZER.serialize(map));
		doReturn(100L).when(serverRequest).getContentLength();
		Route route = mock(Route.class);
		RouteConfiguration configuration = mock(RouteConfiguration.class);
		when(configuration.getSerializer(any(MediaType.class))).thenReturn(Serializer.DEFAULT_JSON_SERIALIZER);
		when(route.getConfiguration()).thenReturn(configuration);
		serverRequest.setResolvedRoute(route);
		Object content = ResourceUtil.getContent(serverRequest, Map.class);
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
		Request request = mock(Request.class);
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		assertEquals(ResourceUtil.invokeAction(model, "dummyAction", new ArrayList<ParameterMetaData>(), request, values), "dummy");
	}
	
	@Test
	public void shouldInvokeMethodWithArguments() throws Throwable {
		Request request = mock(Request.class);
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("value", "test123");
		content.put("someNumber", 1L);
		when(request.getContentAs(Map.class)).thenReturn(content);
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		assertEquals(ResourceUtil.invokeAction(model, "dummyActionWithArguments", Lists.newArrayList(new ParameterMetaData("value", "value", String.class), 
				new ParameterMetaData("someNumber", "someNumber", Long.class)), request, values), "dummyActionWithArguments");
	}
	
	@Test
	public void shouldInvokeMethodWithArgumentsAndModel() throws Throwable {
		Request request = mock(Request.class);
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("value", "test123");
		content.put("someNumber", 1L);
		when(request.getContentAs(Map.class)).thenReturn(content);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("anotherModel", new DummyModel());
		DummyModel model = new DummyModel();
		assertEquals(ResourceUtil.invokeAction(model, "dummyActionWithArgumentsAndModel", Lists.newArrayList(new ParameterMetaData("anotherModel", "anotherModel", DummyModel.class),
				new ParameterMetaData("value", "value", String.class), new ParameterMetaData("someNumber", "someNumber", Long.class)), request, values), "dummyActionWithArgumentsAndModel");
	}
	
	@Test(expectedExceptions=MinnalException.class)
	public void shouldThrowExceptionIfMethodNotFound() throws Throwable {
		Request request = mock(Request.class);
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		ResourceUtil.invokeAction(model, "nonExistingMethod", Lists.newArrayList(new ParameterMetaData("anotherModel", "anotherModel", DummyModel.class)), request, values);
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldThrowExceptionIfMethodThrowsAnyException() throws Throwable {
		Request request = mock(Request.class);
		Map<String, Object> values = new HashMap<String, Object>();
		DummyModel model = new DummyModel();
		ResourceUtil.invokeAction(model, "throwsException", new ArrayList<ParameterMetaData>(), request, values);
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
