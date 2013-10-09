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
import java.util.Arrays;
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
import org.minnal.core.route.RouteBuilder;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
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
		wrapper = spy(new ResourceWrapper(resourceClass));
		EntityNode node = new EntityNode(Parent.class);
		node.construct();
		EntityNode child = node.getChildren().iterator().next();
		path = node.new EntityNodePath(Arrays.asList(node, child));
		rootPath = node.new EntityNodePath(Arrays.asList(node));
	}
	
	@Test
	public void shouldCreateListMethodForAPath() throws Exception {
		Template listMethodCreator = mock(Template.class);
		doReturn(listMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, true)), eq(HttpMethod.GET));
		doReturn("listMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(listMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateCreateMethodForAPath() throws Exception {
		Template createMethodCreator = mock(Template.class);
		doReturn(createMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, true)), eq(HttpMethod.POST));
		doReturn("createMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(createMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateReadMethodForAPath() throws Exception {
		Template readMethodCreator = mock(Template.class);
		doReturn(readMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.GET));
		doReturn("readMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(readMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateUpdateMethodForAPath() throws Exception {
		Template updateMethodCreator = mock(Template.class);
		doReturn(updateMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.PUT));
		doReturn("updateMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(updateMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateDeleteMethodForAPath() throws Exception {
		Template deleteMethodCreator = mock(Template.class);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		doReturn("deleteMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(deleteMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateActionMethodAtRoot() throws Exception {
		Template actionMethodCreator = mock(Template.class);
		doReturn(actionMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(((EntityNode) rootPath.get(0)).getEntityNodePath(""), "dummy")), eq(HttpMethod.PUT));
		doReturn(actionMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(((EntityNode) rootPath.get(0)).getEntityNodePath("children"), "dummy")), eq(HttpMethod.PUT));
		doReturn("actionMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(rootPath);
		verify(actionMethodCreator, times(2)).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldNotCreateMethodIfAlreadyFoundInResource() throws Exception {
		Template deleteMethodCreator = mock(Template.class);
		when(resourceClass.hasRoute(path.getSinglePath(), HttpMethod.DELETE)).thenReturn(true);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		doReturn("deleteMethod").when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		verify(deleteMethodCreator, never()).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldWrapResourceClass() throws Exception {
		Template deleteMethodCreator = mock(Template.class);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		RouteBuilder builder = mock(RouteBuilder.class);
		when(resourceClass.builder(path.getSinglePath().substring("/parents".length()))).thenReturn(builder);
		when(resourceClass.builder(path.getBulkPath().substring("/parents".length()))).thenReturn(mock(RouteBuilder.class));
		final AtomicInteger integer = new AtomicInteger(0);
		doAnswer(new Answer<String>() {
			public String answer(InvocationOnMock invocation) throws Throwable {
				int val = integer.incrementAndGet();
				if (val == 2) {
					return "readParentChild";
				} else if (val == 4) {
					return "updateParentChild";
				} else {
					return "dummy";
				}
			}
		}).when(wrapper).makeMethod(any(StringWriter.class));
		wrapper.addPath(path);
		wrapper.wrap();
		verify(resourceClass).setResourceClass(Class.forName(DummyResource.class.getName() + "Wrapper"));
		verify(builder).action(HttpMethod.GET, "readParentChild", Void.class, Child.class);
		verify(builder).action(HttpMethod.PUT, "updateParentChild", Child.class, Void.class);
	}
}

class DummyResource {
	
}

@Entity
@AggregateRoot
class Parent extends Model {
	@Id
	private Long id;
	@OneToMany
	private Set<Child> children;
	@Override
	public Serializable getId() {
		return id;
	}
	@Action(value="dummy")
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
