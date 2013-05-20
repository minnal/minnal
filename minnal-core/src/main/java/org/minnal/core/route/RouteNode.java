/**
 * 
 */
package org.minnal.core.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.route.RouteNode.RouteNodePath;
import org.minnal.core.route.RoutePattern.RouteElement;
import org.minnal.core.util.Node;

/**
 * A node in the route tree that represents a route element. Route tree enables efficient route resolution of a request. 
 * It is an n'ary tree that stores all the available routes in the application. The path from root to a node can represent 
 * a route and will be stored as node value.
 * <p>
 * A sample tree will look like this,
 * 
 * <ul>
 * 	<li>'/'
 * 		<ul>
 * 			<li>'orders'
 * 				<ul>
 * 					<li>'{order_id}'
 * 						<ul>
 * 							<li>'order_items'
 * 								<ul>
 * 									<li>{id}</li>
 * 								</ul>
 * 							</li>
 * 							<li>'payments'
 * 								<ul>
 * 									<li>{id}</li>
 * 								</ul>
 * 							</li>
 * 						</ul>
 * 					</li>
 * 				</ul>
 * 			</li>
 * 			<li>'products'
 * 				<ul>
 * 					<li>'{id}'</li>
 * 				</ul>	
 * 			</li>
 * 		</ul>
 * 	</li>
 * </ul>
 * 
 * @author ganeshs
 *
 */
public class RouteNode extends Node<RouteNode, RouteNodePath, RouteElement> {
	
	private Map<HttpMethod, Route> routes = new HashMap<HttpMethod, Route>();
	
	/**
	 * Constructs a route node from the route element name
	 * 
	 * @param element
	 */
	public RouteNode(String element) {
		this(new RouteElement(element));
	}
	
	/**
	 * Constructs a route node from the route element
	 * 
	 * @param element
	 */
	public RouteNode(RouteElement element) {
		super(element);
	}
	
	/**
	 * Adds the child to this node. If the element is a parameter, then adds to the tail else to the head.
	 * 
	 * @param element the route element
	 * @return
	 */
	public RouteNode addChild(RouteElement element) {
		RouteNode child = findChild(element.getName(), element.isParameter());
		if (child != null) {
			return child;
		}
		return addChild(new RouteNode(element), !element.isParameter());
	}
	
	/**
	 * Searches for a child node with the given element name. If one doesn't exist returns the parameter node if one exist else returns null
	 * 
	 * @param element
	 * @return
	 */
	public RouteNode findChild(String element) {
		return findChild(element, true);
	}
	
	/**
	 * Searches for a child with the given element. If a child doesn't exist with the name, returns the parameter node if one exists else null
	 * 
	 * @param name
	 * @return
	 */
	protected RouteNode findChild(String element, boolean matchParameter) {
		for (RouteNode child : getChildren()) {
			if (child.getElement().getName().equals(element)) {
				return child;
			}
		}
		if (! getChildren().isEmpty()) {
			RouteNode lastNode = getChildren().getLast();
			if (matchParameter && lastNode.getElement().isParameter()) {
				return lastNode;
			}
		}
		return null;
	}
	
	public void addRoute(Route route) {
		routes.put(route.getMethod(), route);
	}

	/**
	 * @return the element
	 */
	public RouteElement getElement() {
		return getValue();
	}

	/**
	 * @return the routes
	 */
	public Map<HttpMethod, Route> getRoutes() {
		return routes;
	}

	@Override
	protected RouteNode getThis() {
		return this;
	}
	
	@Override
	protected RouteNodePath createNodePath(List<RouteNode> path) {
		return new RouteNodePath(path);
	}
	
	public class RouteNodePath extends Node<RouteNode, RouteNodePath, RouteElement>.NodePath {

		public RouteNodePath(List<RouteNode> path) {
			super(path);
		}
		
	}
}
