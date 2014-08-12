/**
 * 
 */
package org.minnal.instrument.entity;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.activejpa.entity.Model;
import org.minnal.instrument.DefaultNamingStrategy;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.utils.route.QueryParam;
import org.minnal.utils.route.QueryParam.Type;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityNodePathTest {
	
	private EntityNode node;
	
	private NamingStrategy namingStrategy = new DefaultNamingStrategy();
	
	@BeforeMethod
	public void setup() {
		node = new EntityNode(Parent.class, namingStrategy);
		node.construct();
	}
	
	@Test
	public void shouldGetBulkUriForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getBulkPath(), "/parents/{parent_id}/children/{child_id}/children");
	}
	
	@Test
	public void shouldGetSingleUriForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getSinglePath(), "/parents/{parent_id}/children/{child_id}/children/{id}");
	}
	
	@Test
	public void shouldGetBulkUriForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getBulkPath(), "/parents");
	}
	
	@Test
	public void shouldGetCrudStatusForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertTrue(path.isCreateAllowed());
		assertFalse(path.isUpdateAllowed());
	}
	
	@Test
	public void shouldGetCrudStatusForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertTrue(path.isCreateAllowed());
		assertFalse(path.isDeleteAllowed());
	}
	
	@Test
	public void shouldGetSingleUriForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getSinglePath(), "/parents/{id}");
	}
	
	@Test
	public void shouldGetSearchParamsForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getQueryParams(), Arrays.asList(queryParam("code", Type.string), queryParam("id", Type.integer), 
				queryParam("children.code", Type.string), queryParam("children.children.code", Type.string), 
				queryParam("children.children.root.code", Type.string), queryParam("children.children.root.id", Type.integer)));
	}
	
	@Test
	public void shouldGetSearchParamsForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child));
		assertEquals(path.getQueryParams(), Arrays.asList(queryParam("children.code", Type.string), queryParam("children.children.code", Type.string), 
				queryParam("children.children.root.code", Type.string), queryParam("children.children.root.id", Type.integer)));
	}
	
	@Test
	public void shouldGetSearchParamsForPathWithAssociations() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getQueryParams(), Arrays.asList(queryParam("children.code", Type.string), 
				queryParam("children.root.code", Type.string), queryParam("children.root.id", Type.integer)));
	}
	
	@Test
	public void shouldGetNameForPathWithMultipleNodes() {
		EntityNode child = node.getChildren().iterator().next();
		EntityNode grandChild = child.getChildren().iterator().next();
		EntityNodePath path = node.createNodePath(Arrays.asList(node, child, grandChild));
		assertEquals(path.getName(), "ParentChildChild");
	}
	
	@Test
	public void shouldGetNameForPathWithSingleNode() {
		EntityNodePath path = node.createNodePath(Arrays.asList(node));
		assertEquals(path.getName(), "Parent");
	}
	
	private QueryParam queryParam(String name, Type type) {
		return new QueryParam(name, type);
	}
	
	@Entity
	@AggregateRoot(update=false)
	private class Parent extends Model {
		@Id
		@Searchable
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@OneToMany
		private Set<Child> children;
		@Override
		public Serializable getId() {
			return id;
		}
	}
	
	@Entity
	private class Child extends Model {
		@Id
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@OneToMany
		@Collection(delete=false)
		private Set<GrandChild> children;
		@Override
		public Serializable getId() {
			return null;
		}
	}
	
	@Entity
	private class GrandChild extends Model {
		@Id
		private Long id;
		@EntityKey
		@Searchable
		private String code;
		@ManyToOne
		private Parent root;
		@Override
		public Serializable getId() {
			return null;
		}
	}
}
