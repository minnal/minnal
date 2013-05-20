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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import javassist.CtClass;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.RouteBuilder;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
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
	
	@BeforeMethod
	public void setup() {
		resourceClass = mock(ResourceClass.class);
		when(resourceClass.getResourceClass()).thenReturn((Class)DummyResource.class);
		wrapper = spy(new ResourceWrapper(resourceClass));
		EntityNode node = new EntityNode(Parent.class);
		node.construct();
		EntityNode child = node.getChildren().iterator().next();
		path = node.new EntityNodePath(Arrays.asList(node, child));
	}
	
	@Test
	public void shouldCreateListMethodForAPath() throws Exception {
		MethodCreator listMethodCreator = mock(ListMethodCreator.class);
		when(listMethodCreator.getMethodName()).thenReturn("listChild");
		doReturn(listMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, true)), eq(HttpMethod.GET));
		wrapper.addPath(path);
		verify(listMethodCreator).create();
	}
	
	@Test
	public void shouldCreateCreateMethodForAPath() throws Exception {
		MethodCreator createMethodCreator = mock(CreateMethodCreator.class);
		when(createMethodCreator.getMethodName()).thenReturn("createChild");
		doReturn(createMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, true)), eq(HttpMethod.POST));
		wrapper.addPath(path);
		verify(createMethodCreator).create();
	}
	
	@Test
	public void shouldCreateReadMethodForAPath() throws Exception {
		MethodCreator readMethodCreator = mock(ReadMethodCreator.class);
		when(readMethodCreator.getMethodName()).thenReturn("readChild");
		doReturn(readMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, false)), eq(HttpMethod.GET));
		wrapper.addPath(path);
		verify(readMethodCreator).create();
	}
	
	@Test
	public void shouldCreateUpdateMethodForAPath() throws Exception {
		MethodCreator updateMethodCreator = mock(UpdateMethodCreator.class);
		when(updateMethodCreator.getMethodName()).thenReturn("updateChild");
		doReturn(updateMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, false)), eq(HttpMethod.PUT));
		wrapper.addPath(path);
		verify(updateMethodCreator).create();
	}
	
	@Test
	public void shouldCreateDeleteMethodForAPath() throws Exception {
		MethodCreator deleteMethodCreator = mock(DeleteMethodCreator.class);
		when(deleteMethodCreator.getMethodName()).thenReturn("deleteChild");
		doReturn(deleteMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		wrapper.addPath(path);
		verify(deleteMethodCreator).create();
	}
	
	@Test
	public void shouldNotCreateMethodIfAlreadyFoundInResource() throws Exception {
		MethodCreator deleteMethodCreator = mock(DeleteMethodCreator.class);
		when(deleteMethodCreator.getMethodName()).thenReturn("deleteChild");
		when(resourceClass.hasRoute(path.getSinglePath(), HttpMethod.DELETE)).thenReturn(true);
		doReturn(deleteMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		wrapper.addPath(path);
		verify(deleteMethodCreator, never()).create();
	}
	
	@Test
	public void shouldWrapResourceClass() throws Exception {
		MethodCreator deleteMethodCreator = mock(DeleteMethodCreator.class);
		when(deleteMethodCreator.getMethodName()).thenReturn("deleteChild");
		doReturn(deleteMethodCreator).when(wrapper).getMethodCreator(any(CtClass.class), eq(new ResourcePath(path, false)), eq(HttpMethod.DELETE));
		RouteBuilder builder = mock(RouteBuilder.class);
		when(resourceClass.builder(path.getSinglePath())).thenReturn(builder);
		when(resourceClass.builder(path.getBulkPath())).thenReturn(mock(RouteBuilder.class));
		wrapper.addPath(path);
		wrapper.wrap();
		verify(resourceClass).setResourceClass(Class.forName(DummyResource.class.getName() + "Wrapper"));
		verify(builder).action(HttpMethod.GET, "readChild");
		verify(builder).action(HttpMethod.PUT, "updateChild");
		verify(builder).action(HttpMethod.DELETE, deleteMethodCreator.getMethodName());
	}
}

class DummyResource {
	
}

@Entity
class Parent extends Model {
	@Id
	private Long id;
	@OneToMany
	private Set<Child> children;
	@Override
	public Serializable getId() {
		return id;
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
