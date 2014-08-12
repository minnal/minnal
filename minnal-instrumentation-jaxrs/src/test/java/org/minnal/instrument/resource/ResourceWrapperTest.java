/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.activejpa.entity.Model;
import org.minnal.instrument.DefaultNamingStrategy;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.resource.ResourceWrapper.ResourcePath;
import org.minnal.instrument.resource.creator.AbstractMethodCreator;
import org.minnal.instrument.resource.creator.ActionMethodCreator;
import org.minnal.instrument.resource.creator.CreateMethodCreator;
import org.minnal.instrument.resource.creator.DeleteMethodCreator;
import org.minnal.instrument.resource.creator.ListMethodCreator;
import org.minnal.instrument.resource.creator.ReadMethodCreator;
import org.minnal.instrument.resource.creator.ResourceClassCreator;
import org.minnal.instrument.resource.creator.UpdateMethodCreator;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaDataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Strings;

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
	
	private NamingStrategy namingStrategy = new DefaultNamingStrategy();
	
	@BeforeMethod
	public void setup() throws Exception {
		resource = ResourceMetaDataProvider.instance().getResourceMetaData(ParentResource.class);
		entityClass = Parent.class;
		EntityNode node = new EntityNode(entityClass, namingStrategy);
		node.construct();
		EntityNode child = node.getChildren().iterator().next();
		path = node.new EntityNodePath(Arrays.asList(node, child));
		rootPath = node.new EntityNodePath(Arrays.asList(node));
	}
	
	@Test
	public void shouldCreateGeneratedClass() {
		wrapper = new ResourceWrapper(resource, entityClass);
		assertNotNull(wrapper.getGeneratedClass());
	}
	
	@Test
	public void shouldCreateResourceClassCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourceClassCreator creator = wrapper.getResourceClassCreator();
		assertEquals(creator.getEntityClass(), entityClass);
		assertEquals(creator.getPath(), resource.getPath());
		assertEquals(creator.getResource(), resource);
	}
	
	@Test
	public void shouldWrapResourceClass() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		wrapper.addPath(path);
		wrapper.wrap();
		assertTrue(ParentResource.class.isAssignableFrom(wrapper.getHandlerClass()));
	}
	
	@Test
	public void shouldGetCreateMethodCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(path, true, namingStrategy);
		CreateMethodCreator creator = wrapper.getCreateMethodCreator(resourcePath);
		assertMethodCreator(creator, resourcePath);
	}
	
	@Test
	public void shouldGetReadMethodCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(path, false, namingStrategy);
		ReadMethodCreator creator = wrapper.getReadMethodCreator(resourcePath);
		assertMethodCreator(creator, resourcePath);
	}
	
	@Test
	public void shouldGetActionMethodCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(path, false, namingStrategy);
		ActionMetaData action = mock(ActionMetaData.class);
		ActionMethodCreator creator = wrapper.getActionMethodCreator(resourcePath, action);
		assertMethodCreator(creator, resourcePath);
		assertEquals(creator.getAction(), action);
	}
	
	@Test
	public void shouldGetDeleteMethodCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(path, false, namingStrategy);
		DeleteMethodCreator creator = wrapper.getDeleteMethodCreator(resourcePath);
		assertMethodCreator(creator, resourcePath);
	}
	
	@Test
	public void shouldGetListMethodCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(path, false, namingStrategy);
		ListMethodCreator creator = wrapper.getListMethodCreator(resourcePath);
		assertMethodCreator(creator, resourcePath);
	}
	
	@Test
	public void shouldGetUpdateMethodCreator() {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(path, false, namingStrategy);
		UpdateMethodCreator creator = wrapper.getUpdateMethodCreator(resourcePath);
		assertMethodCreator(creator, resourcePath);
	}
	
	public void assertMethodCreator(AbstractMethodCreator creator, ResourcePath resourcePath) {
		assertNotNull(creator.getCtClass());
		assertEquals(creator.getBasePath(), resource.getPath());
		assertEquals(creator.getResource(), resource);
		assertEquals(creator.getResourcePath(), resourcePath);
	}
	
	@Test
	public void shouldAddActionMethods() throws Exception {
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ResourcePath resourcePath = new ResourcePath(rootPath, false, namingStrategy);
		Set<ActionMetaData> actions = resourcePath.getNodePath().get(0).getEntityMetaData().getActionMethods();
		ActionMethodCreator creator = mock(ActionMethodCreator.class);
		EntityNode node = resourcePath.getNodePath().get(0);
		for (ActionMetaData action : actions) {
			if (! Strings.isNullOrEmpty(action.getPath())) {
				continue;
			}
			doReturn(creator).when(wrapper).getActionMethodCreator(new ResourcePath(node.getEntityNodePath(action.getPath()), action.getName(), namingStrategy), action);
			break;
		}
		wrapper.addActionMethods(resourcePath);
		verify(creator).create();
		verify(wrapper, times(2)).getActionMethodCreator(any(ResourcePath.class), any(ActionMetaData.class));
	}
	
	@Test
	public void shouldAddGetPaths() throws Exception {
		EntityNodePath path = mock(EntityNodePath.class);
		when(path.isReadAllowed()).thenReturn(true);
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		ReadMethodCreator creator1 = mock(ReadMethodCreator.class);
		ListMethodCreator creator2 = mock(ListMethodCreator.class);
		doReturn(creator1).when(wrapper).getReadMethodCreator(new ResourcePath(path, false, namingStrategy));
		doReturn(creator2).when(wrapper).getListMethodCreator(new ResourcePath(path, true, namingStrategy));
		wrapper.addPath(path);
		verify(creator1).create();
		verify(creator2).create();
	}
	
	@Test
	public void shouldAddPostPaths() throws Exception {
		EntityNodePath path = mock(EntityNodePath.class);
		when(path.isCreateAllowed()).thenReturn(true);
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		CreateMethodCreator creator = mock(CreateMethodCreator.class);
		doReturn(creator).when(wrapper).getCreateMethodCreator(new ResourcePath(path, true, namingStrategy));
		wrapper.addPath(path);
		verify(creator).create();
	}
	
	@Test
	public void shouldAddDeletePaths() throws Exception {
		EntityNodePath path = mock(EntityNodePath.class);
		when(path.isDeleteAllowed()).thenReturn(true);
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		DeleteMethodCreator creator = mock(DeleteMethodCreator.class);
		doReturn(creator).when(wrapper).getDeleteMethodCreator(new ResourcePath(path, false, namingStrategy));
		wrapper.addPath(path);
		verify(creator).create();
	}
	
	@Test
	public void shouldAddUpdatePaths() throws Exception {
		EntityNodePath path = mock(EntityNodePath.class);
		when(path.isUpdateAllowed()).thenReturn(true);
		wrapper = spy(new ResourceWrapper(resource, entityClass));
		UpdateMethodCreator creator = mock(UpdateMethodCreator.class);
		doReturn(creator).when(wrapper).getUpdateMethodCreator(new ResourcePath(path, false, namingStrategy));
		doNothing().when(wrapper).addActionMethods(new ResourcePath(path, false, namingStrategy));
		wrapper.addPath(path);
		verify(creator).create();
		verify(wrapper).addActionMethods(new ResourcePath(path, false, namingStrategy));
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
	public void dummyAction() {
	}
}
