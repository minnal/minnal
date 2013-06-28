/**
 * 
 */
package org.minnal.core.route;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.minnal.core.route.QueryParam.Type;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RouteActionTest {
	
	private RouteAction action;
	
	private Route route;
	
	@BeforeMethod
	public void setup() {
		route = mock(Route.class);
		action = new RouteAction(route);
	}

	@Test
	public void shouldAddQueryParamsWithName() {
		action.queryParam("param1");
		verify(route).addQueryParam(new QueryParam("param1", Type.string));
	}
	
	@Test
	public void shouldAddQueryParamsWithNameAndType() {
		action.queryParam("param1", Type.integer, "test param");
		verify(route).addQueryParam(new QueryParam("param1", Type.string, "test param"));
	}
	
	@Test
	public void shouldAddQueryParamsWithNameAndDescription() {
		action.queryParam("param1", "test param");
		verify(route).addQueryParam(new QueryParam("param1", Type.string, "test param"));
	}
	
	@Test
	public void shouldAddMultipleAttributes() {
		action.attribute("testKey1", "testValue1");
		action.attribute("testKey2", "testValue2");
		verify(route).addAttribute("testKey1", "testValue1");
		verify(route).addAttribute("testKey2", "testValue2");
	}
	
	@Test
	public void shouldAddAttributeMap() {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("testKey1", "testValue1");
		attributes.put("testKey2", "testValue2");
		action.attributes(attributes);
		action.attribute("testKey3", "testValue3");
		verify(route).setAttributes(attributes);
	}
}
