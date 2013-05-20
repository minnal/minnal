/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.minnal.core.resource.ResourceClass;
import org.minnal.instrument.entity.EntityNode;
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
		when(wrapper.getResourceClass()).thenReturn(resourceClass);
	}

	@Test
	public void shouldEnhanceResource() {
		ResourceEnhancer enhancer = new ResourceEnhancer(wrapper);
		enhancer.enhance();
		EntityNode node = new EntityNode(Parent.class);
		node.construct();
		EntityNodePath path = node.new EntityNodePath(Arrays.asList(node));
		verify(wrapper, times(3)).addPath(any(EntityNodePath.class));
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
