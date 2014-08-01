/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.activejpa.entity.Model;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.resource.ResourceWrapper.HTTPMethod;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaDataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceWrapperTest {
	
	private ResourceMetaData resource;
	
	private Class<?> entityClass;
	
	private ResourceWrapper wrapper;
	
	private EntityNodePath path;
	
	private EntityNodePath rootPath;
	
	@BeforeMethod
	public void setup() throws Exception {
		resource = ResourceMetaDataProvider.instance().getResourceMetaData(ParentResource.class);
		entityClass = Parent.class;
		EntityNode node = new EntityNode(entityClass);
		node.construct();
		EntityNode child = node.getChildren().iterator().next();
		path = node.new EntityNodePath(Arrays.asList(node, child));
		rootPath = node.new EntityNodePath(Arrays.asList(node));
	}
	
	@Test
	public void shouldCreateListMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template listMethodCreator = mock(Template.class);
		ResourcePath resourcePath = new ResourcePath(path, true);
		doReturn(listMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath), eq(HTTPMethod.get));
		doReturn("listMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.get), eq(resourcePath));
		wrapper.addPath(path);
		verify(listMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateCreateMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template createMethodCreator = mock(Template.class);
		ResourcePath resourcePath = new ResourcePath(path, true);
		doReturn(createMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath), eq(HTTPMethod.post));
		doReturn("createMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.post), eq(resourcePath));
		wrapper.addPath(path);
		verify(createMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateReadMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template readMethodCreator = mock(Template.class);
		ResourcePath resourcePath = new ResourcePath(path, false);
		doReturn(readMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath), eq(HTTPMethod.get));
		doReturn("readMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.get), eq(resourcePath));
		wrapper.addPath(path);
		verify(readMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateUpdateMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template updateMethodCreator = mock(Template.class);
		ResourcePath resourcePath = new ResourcePath(path, false);
		doReturn(updateMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath), eq(HTTPMethod.put));
		doReturn("updateMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.put), eq(resourcePath));
		wrapper.addPath(path);
		verify(updateMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateDeleteMethodForAPath() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template deleteMethodCreator = mock(Template.class);
		ResourcePath resourcePath = new ResourcePath(path, false);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath), eq(HTTPMethod.delete));
		doReturn("deleteMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.delete), eq(resourcePath));
		wrapper.addPath(path);
		verify(deleteMethodCreator).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldCreateActionMethodAtRoot() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template actionMethodCreator = mock(Template.class);
		ResourcePath resourcePath1 = new ResourcePath(((EntityNode) rootPath.get(0)).getEntityNodePath(""), "dummy");
		ResourcePath resourcePath2 = new ResourcePath(((EntityNode) rootPath.get(0)).getEntityNodePath("children"), "dummy");
		doReturn(actionMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath1), eq(HTTPMethod.put));
		doReturn(actionMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath2), eq(HTTPMethod.put));
		doReturn("actionMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.put), eq(resourcePath1));
		doReturn("actionMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.put), eq(resourcePath2));
		wrapper.addPath(rootPath);
		verify(actionMethodCreator, times(2)).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldNotCreateMethodIfAlreadyFoundInResource() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		Template deleteMethodCreator = mock(Template.class);
		when(wrapper.hasRoute(path.getSinglePath(), HTTPMethod.delete)).thenReturn(true);
		ResourcePath resourcePath = new ResourcePath(path, false);
		doReturn(deleteMethodCreator).when(wrapper).getMethodTemplate(eq(resourcePath), eq(HTTPMethod.delete));
		doReturn("deleteMethod").when(wrapper).makeMethod(any(StringWriter.class), eq(HTTPMethod.delete), eq(resourcePath));
		wrapper.addPath(path);
		verify(deleteMethodCreator, never()).merge(any(VelocityContext.class), any(StringWriter.class));
	}
	
	@Test
	public void shouldWrapResourceClass() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		wrapper.addPath(path);
		wrapper.wrap();
		assertTrue(ParentResource.class.isAssignableFrom(wrapper.getHandlerClass()));
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
@Path("/parents")
class ParentResource {
	@GET
	public void get() {};
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
