/**
 * 
 */
package org.minnal.core.route;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.route.RoutePattern.RouteElement;
import org.minnal.core.util.Node.Visitor;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RouteNodeTest {
	
	@Test
	public void shouldCreateNodeFromElementName() {
		RouteNode node = new RouteNode("/");
		assertEquals(node.getElement().getName(), "/");
	}
	
	@Test
	public void shouldCreateNodeFromElement() {
		RouteNode node = new RouteNode(new RouteElement("/"));
		assertEquals(node.getElement().getName(), "/");
	}
	
	@Test
	public void shouldAddChildNode() {
		RouteNode node = new RouteNode("/");
		node.addChild(new RouteElement("orders"));
		assertEquals(node.getChildren().size(), 1);
		assertEquals(node.getChildren().get(0).getElement().getName(), "orders");
	}
	
	@Test
	public void shouldSetParentToChild() {
		RouteNode node = new RouteNode("/");
		RouteNode child = node.addChild(new RouteElement("orders"));
		assertEquals(child.getParent(), node);
	}
	
	@Test
	public void shouldAddParameterizedChildNodeToTail() {
		RouteNode node = new RouteNode("/");
		node.addChild(new RouteElement("orders"));
		node.addChild(new RouteElement("{id}", true));
		node.addChild(new RouteElement("products"));
		node.addChild(new RouteElement("payments"));
		assertEquals(node.getChildren().size(), 4);
		assertEquals(node.getChildren().get(3).getElement().getName(), "{id}");
	}
	
	@Test
	public void shouldAddRouteToANode() {
		RouteNode node = new RouteNode("/");
		node.addRoute(new Route(new RoutePattern("/dummy"), HttpMethod.GET, null, null, null));
		node.addRoute(new Route(new RoutePattern("/dummy"), HttpMethod.POST, null, null, null));
		assertEquals(node.getRoutes().size(), 2);
	}
	
	@Test
	public void shouldFindNode() {
		RouteNode node = new RouteNode("/");
		node.addChild(new RouteElement("orders"));
		node.addChild(new RouteElement("{id}", true));
		node.addChild(new RouteElement("products"));
		assertEquals(node.findChild("orders").getElement().getName(), "orders");
	}
	
	@Test
	public void shouldFindParameterizedNodeWithMatchParameter() {
		RouteNode node = new RouteNode("/");
		node.addChild(new RouteElement("orders"));
		node.addChild(new RouteElement("{id}", true));
		node.addChild(new RouteElement("products"));
		assertEquals(node.findChild("{order_id}").getElement().getName(), "{id}");
	}
	
	@Test
	public void shouldFindParameterizedNodeForNonExistingElement() {
		RouteNode node = new RouteNode("/");
		node.addChild(new RouteElement("orders"));
		node.addChild(new RouteElement("{id}", true));
		node.addChild(new RouteElement("products"));
		assertEquals(node.findChild("1").getElement().getName(), "{id}");
	}
	
	@Test
	public void shouldInvokeVistorOnTraverse() {
		final List<String> visitedNodes = new ArrayList<String>();
		Visitor<RouteNode> visitor = new Visitor<RouteNode>() {
			public void visit(RouteNode node) {
				visitedNodes.add(node.getElement().getName());
			}
		};
		RouteNode node = new RouteNode("/");
		node.addChild(new RouteElement("orders"));
		node.addChild(new RouteElement("{order_id}", true));
		node.addChild(new RouteElement("products"));
		node.findChild("orders").addChild(new RouteElement("order_items"));
		node.findChild("orders").addChild(new RouteElement("{id}"));
		node.traverse(visitor);
		assertTrue(visitedNodes.containsAll(Arrays.asList("/", "{order_id}", "{id}", "products", "order_items", "orders")));
	}

}
