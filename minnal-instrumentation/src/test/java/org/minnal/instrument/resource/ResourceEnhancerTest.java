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
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.minnal.core.resource.ResourceClass;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceEnhancerTest {
	
	private ResourceWrapper wrapper;
	
	private ResourceClass resourceClass;
	
	@BeforeMethod
	public void setup() {
		wrapper = mock(ResourceWrapper.class);
		resourceClass = mock(ResourceClass.class);
		when(resourceClass.getEntityClass()).thenReturn((Class)Parent.class);
	}

	@Test
	public void shouldEnhanceResource() {
		ResourceEnhancer enhancer = spy(new ResourceEnhancer(resourceClass));
		doReturn(wrapper).when(enhancer).createResourceWrapper(resourceClass);
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
}
