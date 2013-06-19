/**
 * 
 */
package org.minnal.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.minnal.core.route.Action;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class FilterChainTest {
	
	private FilterChain chain;
	
	private TestFilter filter1;
	
	private Filter filter2;
	
	private Filter filter3;
	
	private Route route;
	
	private Action action;
	
	private ServerRequest request;
	
	private ServerResponse response;
	
	private MessageContext context;
	
	private RouteResolver resolver;
	
	@BeforeMethod
	public void setup() {
		request = mock(ServerRequest.class);
		response = mock(ServerResponse.class);
		context = new MessageContext(request, response);
		filter1 = spy(new TestFilter());
		filter2 = spy(new TestFilter());
		filter3 = spy(new TestFilter());
		route = mock(Route.class);
		action = mock(Action.class);
		resolver = mock(RouteResolver.class);
		when(resolver.resolve(context)).thenReturn(route);
		when(route.getAction()).thenReturn(action);
		when(action.invoke(request, response)).thenReturn(Arrays.asList(""));
		chain = new FilterChain(Arrays.asList(filter1, filter2, filter3), resolver);
	}

	@Test
	public void shouldInvokeAllFiltersBeforeCallingAction() {
		chain.doFilter(context);
		verify(filter1).doFilter(request, response, chain);
		verify(filter2).doFilter(request, response, chain);
		verify(filter3).doFilter(request, response, chain);
		verify(action).invoke(request, response);
	}
	
	@Test
	public void shouldInvokePostActionOperation() {
		chain.doFilter(context);
		verify((TestFilter)filter1).postDelegation();
		verify((TestFilter)filter2).postDelegation();
		verify((TestFilter)filter3).postDelegation();
		verify(action).invoke(request, response);
	}
	
	@Test
	public void shouldSetResponseIfNotSetAlready() {
		when(response.isContentSet()).thenReturn(false);
		chain.doFilter(context);
		verify(response).setContent(Arrays.asList(""));
	}
	
	@Test
	public void shouldNotSetResponseIfSetAlready() {
		when(response.isContentSet()).thenReturn(true);
		chain.doFilter(context);
		verify(response, never()).setContent(Arrays.asList(""));
	}
	
	@Test
	public void shouldNotSetResponseIfResponseIsNull() {
		when(action.invoke(request, response)).thenReturn(null);
		when(response.isContentSet()).thenReturn(true);
		chain.doFilter(context);
		verify(response, never()).setContent(null);
	}
	
	@Test
	public void shouldNotCallActionIfOneOfTheFiltersDoesntForwardControl() {
		filter1 = spy(new TestFilter());
		filter2 = spy(new TestFilter(false));
		filter3 = spy(new TestFilter());
		chain = new FilterChain(Arrays.asList(filter1, filter2, filter3), resolver);
		chain.doFilter(context);
		verify(filter1).doFilter(request, response, chain);
		verify(filter2).doFilter(request, response, chain);
		verify(filter3, never()).doFilter(request, response, chain);
		verify(action, never()).invoke(request, response);
	}
	
	private class TestFilter implements Filter {
		
		private boolean callAction;
		
		public TestFilter() {
			this(true);
		}
		
		public TestFilter(boolean callAction) {
			this.callAction = callAction;
		}
		
		public void doFilter(Request request, Response response, FilterChain chain) {
			if (callAction) {
				chain.doFilter(request, response);
			}
			postDelegation();
		}
		
		public void postDelegation() {}
	}
}
