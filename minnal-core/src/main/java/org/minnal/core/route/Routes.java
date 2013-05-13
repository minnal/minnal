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
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.exception.MethodNotAllowedException;
import org.minnal.core.server.exception.NotAcceptableException;
import org.minnal.core.server.exception.NotFoundException;
import org.minnal.core.server.exception.UnsupportedMediaTypeException;
import org.minnal.core.util.Node.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

/**
 * Manages all the routes in the application. Constructs a route tree for from the available routes for efficient resolution
 * 
 * @author ganeshs
 *
 */
public class Routes {

	private RouteNode root = new RouteNode("/");
	
	private static final Logger logger = LoggerFactory.getLogger(Routes.class);
	
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
	 * Resolves the request by looking up the route tree and returns a route matching the request path and method.
	 * <ul>
	 * <li> If no route matches, throws {@link NotFoundException}. 
	 * <li> If http method doesn't match, throws {@link MethodNotAllowedException}. 
	 * <li> If Content-Type is not provided/supported (for POST and PUT methods), throws {@link UnsupportedMediaTypeException}. 
	 * <li> If response format is not acceptable to client, throws {@link NotAcceptableException}
	 * </ul>
	 * 
	 * @param request
	 * @return
	 */
	public Route resolve(final ServerRequest request) {
		logger.trace("Resolving the route for the request {}", request);
		RouteNode node = findNode(request.getRelativePath());
		if (node == null) {
			throw new NotFoundException();
		}
		Route route = node.getRoutes().get(request.getHttpMethod());
		if (route == null) {
			throw new MethodNotAllowedException(node.getRoutes().keySet());
		}
		if (shouldCheckContentType(request) && (request.getContentType() == null || ! route.getConfiguration().supportsMediaType(request.getContentType()))) {
			throw new UnsupportedMediaTypeException(route.getConfiguration().getSupportedMediaTypes());
		}
		Set<MediaType> supportedAccepts = Collections.unmodifiableSet(Sets.filter(route.getConfiguration().getSupportedMediaTypes(), new Predicate<MediaType>() {
			public boolean apply(MediaType input) {
				if (request.getAccepts().isEmpty()) {
					return true;
				}
				for (MediaType mediaType : request.getAccepts()) {
					if (input.is(mediaType)) {
						return true;
					}
				}
				return false;
			}
		}));
		if (supportedAccepts.isEmpty()) {
			throw new NotAcceptableException(route.getConfiguration().getSupportedMediaTypes());
		}
		request.setSupportedAccepts(supportedAccepts);
		return route;
	}
	
	private boolean shouldCheckContentType(Request request) {
		return request.getHttpMethod() == HttpMethod.POST || request.getHttpMethod().equals(HttpMethod.PUT); 
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
		if (! node.getRoutes().isEmpty()) {
			return node;
		}
		return null;
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
