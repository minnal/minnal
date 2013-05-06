/**
 * 
 */
package org.minnal.core.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.Request;
import org.minnal.core.route.RoutePattern.RouteElement;
import org.minnal.core.util.Node.Visitor;

/**
 * Manages all the routes in the application. Constructs a route tree for from the available routes for efficient resolution
 * 
 * @author ganeshs
 *
 */
public class Routes {

	private RouteNode root = new RouteNode("/");
	
	/**
	 * Constructs an instance with default root
	 */
	public Routes() {
		this(new RouteNode("/"));
	}
	
	/**
	 * Constructs an instances with the given root
	 * 
	 * @param root
	 */
	public Routes(RouteNode root) {
		this.root = root;
	}
	
	/**
	 * Returns all the routes in the application
	 * 
	 * @return
	 */
	public List<Route> getRoutes() {
		final List<Route> routes = new ArrayList<Route>();
		root.traverse(new Visitor<RouteNode>() {
			public void visit(RouteNode node) {
				routes.addAll(node.getRoutes().values());
			}
		});
		return routes;
	}
	
	/**
	 * Returns the allowed http methods for the given request. If the request doesn't resolve to a route, returns an empty set
	 * 
	 * @param request
	 * @return
	 */
	public Set<HttpMethod> getAllowedMethods(Request request) {
		RouteNode node = findNode(request.getRelativePath());
		if (node != null) {
			return Collections.unmodifiableSet(node.getRoutes().keySet());
		}
		return Collections.emptySet();
	}
	
	/**
	 * Resolves the request by looking up the route tree and returns a route matching the request path and method. If no route matches, returns null
	 * 
	 * @param request
	 * @return
	 */
	public Route resolve(Request request) {
		RouteNode node = findNode(request.getRelativePath());
		if (node != null) {
			return node.getRoutes().get(request.getHttpMethod());
		}
		return null;
	}
	
	/**
	 * Finds the node that matches the given path. If a match for the path is found in the tree, the node corresponding to the last element
	 * of the path will be returned back. In case a match is not found, returns null
	 * 
	 * @param path
	 * @return
	 */
	protected RouteNode findNode(String path) {
		String[] pathElements = path.substring(1).split("/");
		RouteNode node = root;
		for (String element : pathElements) {
			node = node.findChild(element);
			if (node == null) {
				return null;
			}
		}
		return node;
	}

	/**
	 * Adds the routes from the given route builder. Traverses the tree and inserts the missing nodes
	 * 
	 * @param builder
	 */
	public void addRoute(RouteBuilder builder) {
		List<Route> routes = builder.build();
		for (Route route : routes) {
			addPath(route);
		}
	}
	
	/**
	 * Inserts a path to the route tree. If a route elements already exists, adds the children to the already existing route element
	 * 
	 * @param route
	 */
	private void addPath(Route route) {
		List<RouteElement> elements = route.getRoutePattern().getElements();
		RouteNode node = root;
		for (RouteElement element : elements) {
			node = node.addChild(element);
		}
		node.addRoute(route);
	}
}
