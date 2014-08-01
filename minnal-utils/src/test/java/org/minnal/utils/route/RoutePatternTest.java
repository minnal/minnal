/**
 * 
 */
package org.minnal.utils.route;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RoutePatternTest {
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAllowPathNotStartingWithSlash() {
		new RoutePattern("orders/1/order_items/1");
	}
	
	@Test
	public void shouldGetRouteElements() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}/order_items");
		assertEquals(pattern.getElements().size(), 3);
		assertEquals(pattern.getElements().get(0).getName(), "orders");
		assertFalse(pattern.getElements().get(0).isParameter());
		assertTrue(pattern.getElements().get(1).isParameter());
		
	}

	@Test
	public void shouldAllowPathPatternWithoutParameters() {
		new RoutePattern("/orders/1/order_items/1");
		new RoutePattern("/Orders/1/order-items/1");
	}
	
	@Test
	public void shouldAllowPathPatternWithParameters() {
		new RoutePattern("/orders/{order_id}/order_items/{id}");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAllowPathPatternWithEmptyParameters() {
		new RoutePattern("/orders/{order_id}/order_items/{}");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAllowParametersWithSpecialCharacters() {
		new RoutePattern("/orders/{/order_items}/id");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldNotAllowParametersWithIncompleteBraces() {
		new RoutePattern("/orders/{order_id/order_items/{id}");
	}
	
	@Test
	public void shouldFetchParameterNames() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}/order_items/{id}");
		assertNotNull(pattern.getParameterNames());
		assertEquals(pattern.getParameterNames(), Arrays.asList("order_id", "id"));
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void shouldAllowPathWithDuplicateParameters() {
		new RoutePattern("/orders/{id}/order_items/{id}");
	}
	
	@Test
	public void shouldMatchAPathWithAlphaNumericParamsToPattern() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}/order_items/{id}");
		assertTrue(pattern.matches("/orders/code1/order_items/1code"));
	}
	
	@Test
	public void shouldMatchAPathWithSpecialCharacterParamsToPattern() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}/order_items/{id}");
		assertTrue(pattern.matches("/orders/order_1/order_items/order-item-1"));
	}
	
	@Test
	public void shouldGetParameterMapForAValidPath() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}/order_items/{id}");
		Map<String, String> params = pattern.match("/orders/1/order_items/124");
		assertNotNull(params);
		assertEquals(params.get("order_id"), "1");
		assertEquals(params.get("id"), "124");
	}
	
	@Test
	public void shouldNotGetParameterMapForAnInvalidPath() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}/order_items/{id}");
		Map<String, String> params = pattern.match("/orders/order_items/124");
		assertNull(params);
	}
	
	@Test
	public void shouldGetEmptyParameterMapForAPathWithoutParameters() {
		RoutePattern pattern = new RoutePattern("/orders/1/order_items/124");
		Map<String, String> params = pattern.match("/orders/1/order_items/124");
		assertNotNull(params);
		assertTrue(params.isEmpty());
	}
	
	@Test
	public void shouldMatchUrlEncodedString() {
		RoutePattern pattern = new RoutePattern("/orders/{order_id}");
		Map<String, String> params = pattern.match("/orders/1234%2F1");
		assertEquals(params.get("order_id"), "1234/1");
	}
 }
