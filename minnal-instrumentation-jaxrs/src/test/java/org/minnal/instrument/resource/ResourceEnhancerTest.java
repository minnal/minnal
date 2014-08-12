/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.activejpa.entity.Model;
import org.minnal.instrument.DefaultNamingStrategy;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaDataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceEnhancerTest {
	
	private ResourceWrapper wrapper;
	
	private ResourceMetaData resource;
	
	private Class<?> entityClass;
	
	private NamingStrategy namingStrategy = new DefaultNamingStrategy();
	
	@BeforeMethod
	public void setup() {
		wrapper = mock(ResourceWrapper.class);
		resource = ResourceMetaDataProvider.instance().getResourceMetaData(DummyResource.class);
		entityClass = Parent.class;
	}

	@Test
	public void shouldEnhanceResource() {
		ResourceEnhancer enhancer = spy(new ResourceEnhancer(resource, entityClass, namingStrategy));
		doReturn(wrapper).when(enhancer).createResourceWrapper();
		enhancer.enhance();
		verify(wrapper, times(2)).addPath(any(EntityNodePath.class));
		verify(wrapper).wrap();
	}
	
	@Entity
	class Parent extends Model {
		@OneToMany
		private Set<Parent> children;
		@Override
		public Serializable getId() {
			return null;
		}
	}
	
	@Path("/")
	class DummyResource {
		@GET
		public void dummyGet() {}
	}
}
