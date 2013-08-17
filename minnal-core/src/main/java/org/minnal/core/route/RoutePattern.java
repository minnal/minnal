/**
 * 
 */
package org.minnal.core.route;

import static org.minnal.core.util.HttpUtil.decode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Splitter;

/**
 * Defines the pattern of a path. Incoming requests will be matched against the route pattern to determine the route.
 * <p/>
 * 
 * Route pattern can contain optional parameter names that will be mapped against the request path. The parameters 
 * should be specified within flower brackets like <code> {order_id}</code> and should be unique for a route. 
 * Few examples on valid route patterns,
 * 
 * <pre>
 * /orders/{order_id}/order_items
 * /orders/{order_id}/order_items/{order_item_id}
 * /orders/1/order_items/1
 * /orders
 * <pre>
 * 
 * @author ganeshs
 *
 */
public class RoutePattern {
	
	private static final String PLACEHOLDER_REGEX = "[a-zA-Z0-9\\-_%,]+";
	
	private static final String PLACEHOLDER_PATTERN_REGEX = "\\{" + PLACEHOLDER_REGEX + "\\}";

	private static final String PATH_PATTERN_REGEX = "(\\/[a-z0-9\\-_\\.A-Z]*(" + PLACEHOLDER_PATTERN_REGEX + ")*)+";
	
	private static final Pattern PATH_PATTERN = Pattern.compile(PATH_PATTERN_REGEX);
	
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(PLACEHOLDER_PATTERN_REGEX);
	
	private String pathPattern;
	
	private Pattern regex;
	
	private List<String> parameterNames = new ArrayList<String>();
	
	/**
	 * Constructs a route pattern. If the route pattern is not valid throws an IllegalArgumentException
	 * 
	 * @param pathPattern
	 */
	public RoutePattern(String pathPattern) {
		this.pathPattern = pathPattern;
		validate();
		compile();
	}
	
	/**
	 * Checks if the route pattern is valid
	 */
	protected void validate() {
		if (! PATH_PATTERN.matcher(pathPattern).matches()) {
			throw new IllegalArgumentException("Invalid pattern - " + pathPattern + ". Doesn't match the regex - " + PATH_PATTERN_REGEX);
		}
	}
	
	/**
	 * Compiles the route pattern, retrieves the parameter names from the path. 
	 */
	protected void compile() {
		Matcher matcher = PLACEHOLDER_PATTERN.matcher(pathPattern);
		while(matcher.find()) {
			String param = matcher.group().replaceAll("\\{|\\}", "");
			if (parameterNames.contains(param)) {
				throw new IllegalArgumentException("Duplicate parameter name - " + param + " detected for the path " + pathPattern);
			}
			parameterNames.add(matcher.group().replaceAll("\\{|\\}", ""));
		}
		regex = Pattern.compile(matcher.replaceAll("(" + PLACEHOLDER_REGEX + ")"));
	}
	
	/**
	 * Matches the given path with the pattern and returns the path parameter map. If the pattern doesn't have parameters, 
	 * returns back an empty map. If the path doesn't match the pattern returns null value.
	 * 
	 * @param path the path to be matched with this pattern
	 * @return
	 */
	public Map<String, String> match(String path) {
		Map<String, String> params = new HashMap<String, String>();
		Matcher matcher = regex.matcher(path);
		if (!matcher.matches()) {
			return null;
		}
		for (int i = 1; i <= matcher.groupCount(); i++) {
			params.put(parameterNames.get(i-1), decode(matcher.group(i)));
		}
		return params;
	}
	
	/**
	 * Checks if the given path matches the pattern
	 * 
	 * @param path
	 * @return
	 */
	public boolean matches(String path) {
		return regex.matcher(path).matches();
	}
	
	/**
	 * @return the parameterNames
	 */
	public List<String> getParameterNames() {
		return parameterNames;
	}

	/**
	 * @return the pathPattern
	 */
	public String getPathPattern() {
		return pathPattern;
	}
	
	public boolean isExactMatch() {
		return parameterNames.isEmpty();
	}
	
	/**
	 * Splits the pattern by '/' into route elements
	 * 
	 * @return the elements
	 */
	@JsonIgnore
	public List<RouteElement> getElements() {
		List<RouteElement> elements = new ArrayList<RoutePattern.RouteElement>();
		for (String element : Splitter.on("/").split(pathPattern)) {
			elements.add(new RouteElement(element, PLACEHOLDER_PATTERN.matcher(element).matches()));
		}
		if (elements.isEmpty()) {
			System.out.println("");
		}
		elements.remove(0); // Remove the root
		return elements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regex == null) ? 0 : regex.pattern().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoutePattern other = (RoutePattern) obj;
		if (regex == null) {
			if (other.regex != null)
				return false;
		} else if (!regex.pattern().equals(other.regex.pattern()))
			return false;
		return true;
	}

	/**
	 * Multiple route elements separated by a '/' consitute a route pattern 
	 * 
	 * @author ganeshs
	 *
	 */
	public static class RouteElement {
		
		private String name;
		
		private boolean parameter;
		
		public RouteElement(String name) {
			this(name, false);
		}
		
		/**
		 * @param name
		 */
		public RouteElement(String name, boolean parameter) {
			this.name = name;
			this.parameter = parameter;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		public boolean isParameter() {
			return parameter;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RouteElement other = (RouteElement) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "RouteElement [name=" + name + ", parameter=" + parameter
					+ "]";
		}
	}
}
