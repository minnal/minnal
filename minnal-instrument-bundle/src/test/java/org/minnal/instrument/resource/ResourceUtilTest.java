/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activejpa.entity.Condition;
import org.activejpa.entity.Filter;
import org.minnal.core.Request;
import org.testng.annotations.Test;

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
}
