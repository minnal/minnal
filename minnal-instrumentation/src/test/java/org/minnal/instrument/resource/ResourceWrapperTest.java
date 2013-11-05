/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.RouteAction;
import org.minnal.core.route.RouteBuilder;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.security.auth.Authorizer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceWrapperTest {
	
	private ResourceClass resourceClass;
	
	private ResourceWrapper wrapper;
	
	private EntityNodePath path;
	
	private EntityNodePath rootPath;
	
	@BeforeMethod
	public void setup() throws Exception {
		resourceClass = mock(ResourceClass.class);
		when(resourceClass.getBasePath()).thenReturn("/parents");
		when(resourceClass.getResourceClass()).thenReturn((Class)DummyResource.class);
		when(resourceClass.getEntityClass()).thenReturn((Class)Parent.class);
		EntityNode node = new EntityNode(Parent.class);
		node.construct();
		EntityNode child = node.getChildren().iterator().next();
		path = node.new EntityNodePath(Arrays.asList(node, child));
		rootPath = node.new EntityNodePath(Arrays.asList(node));
	}
	
	@Test
	public void shouldCreateListMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template listMethodCreator = mock(Template.class);
		doReturn(listMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, true)), eq(HttpMethod.GET));
		doReturn("listMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(listMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateCreateMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template createMethodCreator = mock(Template.class);
		doReturn(createMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, true)), eq(HttpMethod.POST));
		doReturn("createMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(createMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateReadMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template readMethodCreator = mock(Template.class);
		doReturn(readMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.GET));
		doReturn("readMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(readMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateUpdateMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template updateMethodCreator = mock(Template.class);
		doReturn(updateMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.PUT));
		doReturn("updateMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(updateMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateDeleteMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template deleteMethodCreator = mock(Template.class);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		doReturn("deleteMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(deleteMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateActionMethodAtRoot() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template actionMethodCreator = mock(Template.class);
		doReturn(actionMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(((EntityNode) rootPath.get(0)).getEntityNodePath(""), "dummy")), eq(HttpMethod.PUT));
		doReturn(actionMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(((EntityNode) rootPath.get(0)).getEntityNodePath("children"), "dummy")), eq(HttpMethod.PUT));
		doReturn("actionMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(rootPath);
		verify(actionMethodCreator, times(2)).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldNotCreateMethodIfAlreadyFoundInResource() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		Template deleteMethodCreator = mock(Template.class);
		when(resourceClass.hasRoute(path.getSinglePath(), HttpMethod.DELETE)).thenReturn(true);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		doReturn("deleteMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(deleteMethodCreator, never()).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldWrapResourceClass() throws Exception {
		wrapper = spy(new ResourceWrapper(resourceClass));
		RouteBuilder builder = mock(RouteBuilder.class);
		when(builder.action(any(HttpMethod.class), any(String.class), any(Type.class), any(Type.class))).thenReturn(mock(RouteAction.class));
		when(builder.action(HttpMethod.GET, "readParentChild", Void.class, Child.class)).thenReturn(mock(RouteAction.class));
		when(builder.action(HttpMethod.PUT, "updateParentChild", Child.class, Void.class)).thenReturn(mock(RouteAction.class));
		when(resourceClass.builder(path.getSinglePath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder(path.getBulkPath().substring("/parents".length()))).thenReturn(builder);
		doAnswer(new MakeMethodAnswer(2, "readParentChild", 4, "updateParentChild")).when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		wrapper.wrap();
		verify(resourceClass).setResourceClass(Class.forName(DummyResource.class.getName() + "Wrapper"));
		verify(builder).action(HttpMethod.GET, "readParentChild", Void.class, Child.class);
		verify(builder).action(HttpMethod.PUT, "updateParentChild", Child.class, Void.class);
	}
	
	@Test
	public void shouldSetPermissionsToSubResourceRoute() throws Exception {
		when(resourceClass.getResourceClass()).thenReturn((Class)DummyResource1.class);
		wrapper = spy(new ResourceWrapper(resourceClass));
		RouteBuilder builder = mock(RouteBuilder.class);
		RouteAction action = mock(RouteAction.class);
		when(builder.action(any(HttpMethod.class), any(String.class), any(Type.class), any(Type.class))).thenReturn(mock(RouteAction.class));
		when(builder.action(HttpMethod.PUT, "updateParentChild", Child.class, Void.class)).thenReturn(action);
		when(resourceClass.builder(path.getSinglePath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder(path.getBulkPath().substring("/parents".length()))).thenReturn(builder);
		doAnswer(new MakeMethodAnswer(2, "readParentChild", 4, "updateParentChild")).when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		wrapper.wrap();
		verify(action).attribute(Authorizer.PERMISSIONS, "permission2");
	}
	
	@Test
	public void shouldSetPermissionsToRootRoute() throws Exception {
		when(resourceClass.getResourceClass()).thenReturn((Class)DummyResource2.class);
		wrapper = spy(new ResourceWrapper(resourceClass));
		RouteBuilder builder = mock(RouteBuilder.class);
		RouteAction action = mock(RouteAction.class);
		when(builder.action(any(HttpMethod.class), any(String.class), any(Type.class), any(Type.class))).thenReturn(mock(RouteAction.class));
		when(builder.action(HttpMethod.POST, "createParent", Parent.class, Parent.class)).thenReturn(action);
		when(resourceClass.builder(rootPath.getSinglePath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder(rootPath.getBulkPath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder("/{parent_id}/children/{id}/dummy")).thenReturn(builder);
		when(resourceClass.builder("/{id}/dummy")).thenReturn(builder);
		doAnswer(new MakeMethodAnswer(3, "createParent")).when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(rootPath);
		wrapper.wrap();
		verify(action).attribute(Authorizer.PERMISSIONS, "permission1");
	}
	
	@Test
	public void shouldSetPermissionsToActionMethod() throws Exception {
		when(resourceClass.getResourceClass()).thenReturn((Class)DummyResource3.class);
		wrapper = spy(new ResourceWrapper(resourceClass));
		RouteBuilder builder = mock(RouteBuilder.class);
		RouteAction parentAction = mock(RouteAction.class);
		RouteAction childAction = mock(RouteAction.class);
		when(builder.action(any(HttpMethod.class), any(String.class), any(Type.class), any(Type.class))).thenReturn(mock(RouteAction.class));
		when(builder.action(HttpMethod.PUT, "dummy", Parent.class, Void.class)).thenReturn(parentAction);
		when(resourceClass.builder(rootPath.getSinglePath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder(rootPath.getBulkPath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder("/{parent_id}/children/{id}/dummy")).thenReturn(builder);
		when(resourceClass.builder("/{id}/dummy")).thenReturn(builder);
		doAnswer(new MakeMethodAnswer(5, "dummy", 6, "dummy")).when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(rootPath);
		wrapper.wrap();
		verify(parentAction).attribute(Authorizer.PERMISSIONS, "permission3");
		verify(childAction, never()).attribute(eq(Authorizer.PERMISSIONS), any(String.class));
	}
	
	private class MakeMethodAnswer implements Answer<String> {
		
		private AtomicInteger integer = new AtomicInteger();
		
		private Map<Integer, String> map = new HashMap<Integer, String>();
		
		public MakeMethodAnswer(Object... args) {
			for (int i = 0; i < args.length; i+=2) {
				map.put((Integer) args[i], (String) args[i+1]);
			}
		}
		
		public String answer(InvocationOnMock invocation) throws Throwable {
			int val = integer.incrementAndGet();
			if (map.containsKey(val)) {
				return map.get(val);
			}
			return "dummy";
		}
	}
}

class DummyResource {
}
class DummyResource1 {
}
class DummyResource2 {
}
class DummyResource3 {
}

@Entity
@AggregateRoot
@Secure(method=Method.POST, permissions="permission1")
class Parent extends Model {
	@Id
	private Long id;
	@OneToMany
	@Secure(method=Method.PUT, permissions="permission2")
	private Set<Child> children;
	@Override
	public Serializable getId() {
		return id;
	}
	@Action(value="dummy")
	@Secure(method=Method.PUT, permissions="permission3")
	public void dummyAction() {
		
	}
	@Action(value="dummy", path="children")
	public void dummyAction(Child child) {
		
	}
}

@Entity
class Child extends Model {
	@Id
	private Long id;
	@OneToMany
	public Serializable getId() {
		return null;
	}
}
