/**
 * 
 */
package org.minnal.api;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.minnal.api.ApiDocumentationNode.ApiDocumentationNodePath;
import org.minnal.core.util.Node;
import org.minnal.utils.reflection.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import scala.collection.JavaConversions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.ModelProperty;

/**
 * @author ganeshs
 *
 */
public class ApiDocumentationNode extends Node<ApiDocumentationNode, ApiDocumentationNodePath, Model> {
	
	private Class<?> clazz;
	
	private String name;
	
	private PropertyDescriptor descriptor;
	
	private Map<Class<?>, List<String>> visitedNodes = new HashMap<Class<?>, List<String>>();
	
	private Map<String, Model> schemas;
	
	private Map<String, Model> models;
	
	private static final Logger logger = LoggerFactory.getLogger(ApiDocumentationNode.class);

	public ApiDocumentationNode(Class<?> clazz) {
		this(clazz, clazz.getSimpleName(), new HashMap<String, Model>(), new HashMap<String, Model>());
	}
	
	public ApiDocumentationNode(Class<?> clazz, String name, Map<String, Model> schemas, Map<String, Model> models) {
		super(new Model());
		this.schemas = schemas;
		this.models = models;
		this.name = name;
		this.clazz = clazz;
		getValue().setId(clazz.getSimpleName());
		getValue().setQualifiedType(clazz.getSimpleName());
	}
	
	public ApiDocumentationNode(PropertyDescriptor descriptor, Map<String, Model> schemas, Map<String, Model> models) {
		this(PropertyUtil.getType(descriptor), descriptor.getName(), schemas, models);
		this.descriptor = descriptor;
		getValue().setId(descriptor.getName());
	}

	@Override
	protected ApiDocumentationNode getThis() {
		return this;
	}

	@Override
	protected ApiDocumentationNodePath createNodePath(List<ApiDocumentationNode> path) {
		return new ApiDocumentationNodePath(path);
	}
	
	public void construct() {
		if (descriptor != null) {
			loadSchema();
		}
		schemas.put(name, getValue());
		if (! PropertyUtil.isSimpleProperty(clazz)) {
			if (! models.containsKey(clazz.getSimpleName())) {
				models.put(clazz.getSimpleName(), getValue());
			}
		} else {
			return;
		}
		
		LinkedList<ApiDocumentationNode> queue = new LinkedList<ApiDocumentationNode>();
		queue.offer(this);
		
		while (! queue.isEmpty()) {
			ApiDocumentationNode node = queue.poll();
			
			for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
				if (descriptor.getName().equals("class")) {
					continue;
				}
				Type genericType = descriptor.getReadMethod() != null ? descriptor.getReadMethod().getGenericReturnType() : 
					descriptor.getWriteMethod() != null ? descriptor.getWriteMethod().getGenericReturnType() : null;
				if (genericType == null) {
					continue;
				}
				Class<?> clazz = PropertyUtil.getType(descriptor);
				if (PropertyUtil.hasAnnotation(descriptor, JsonIgnore.class, true) || PropertyUtil.hasAnnotation(descriptor, JsonBackReference.class, true)) {
					if (! models.containsKey(clazz.getSimpleName())) {
						ApiDocumentationNode child = new ApiDocumentationNode(clazz, clazz.getSimpleName(), new HashMap<String, Model>(), models);
						child.construct();
					}
				} else {
					ApiDocumentationNode child = new ApiDocumentationNode(descriptor, schemas, models);
					if (node.addChild(child) != null) {
						queue.offer(child);
						child.construct();
					}
				}
			}
		}
		LinkedHashMap<String, ModelProperty> properties = new LinkedHashMap<String, ModelProperty>();
		int i = 0;
		for (ApiDocumentationNode node : getChildren()) {
			properties.put(node.getValue().getId(), createModelProperty(node.getValue(), i++));
		}
		getValue().setProperties(properties);
	}
	
	protected ModelProperty createModelProperty(Model model, int position) {
		return new ModelProperty(model.getQualifiedType(), model.getQualifiedType(), position, false, Option.apply(""), null, null);
	}
	
	public Map<String, Model> getModels() {
		return models;
	}
	
	@Override
	protected boolean visited(ApiDocumentationNode node) {
		List<String> associations = visitedNodes.get(node.clazz);
		if (associations == null) {
			return false;
		}
		return associations.contains(node.getValue().getId());
	}
	
	@Override
	protected void markVisited(ApiDocumentationNode node) {
		logger.debug("Marking the node {} as visited in this node {}", node, this);
		List<String> associations = visitedNodes.get(node.clazz);
		if (associations == null) {
			associations = new ArrayList<String>();
			visitedNodes.put(node.clazz, associations);
		}
		associations.add(node.getValue().getId());
	}
	
	private void loadSchema() {
		Model model = null;
		Model schema = getValue();
		
		clazz = PropertyUtil.getType(descriptor);
		if (PropertyUtil.isSimpleProperty(descriptor.getPropertyType())) {
			if (Enum.class.isAssignableFrom(descriptor.getPropertyType())) {
				schema.setQualifiedType("string");
				AllowableListValues values = new AllowableListValues(JavaConversions.asScalaBuffer(PropertyUtil.getEnumValues(descriptor)).toList(), descriptor.getPropertyType().toString());
				schema.setAllowableValues(values);
			} else {
				schema.setQualifiedType(descriptor.getPropertyType().getSimpleName());
			}
		} else if (PropertyUtil.isCollectionProperty(descriptor.getReadMethod().getGenericReturnType(), true)) {
			if (! PropertyUtil.isSimpleProperty(clazz)) {
				if (! schemas.containsKey(descriptor.getName())) {
					ApiDocumentationNode node = new ApiDocumentationNode(clazz, descriptor.getName(), schemas, models);
					node.construct();
					model = node.getValue();
				} else {
					model = schemas.get(descriptor.getName());
				}
				
				schema.setQualifiedType("Array");
				schema.setBaseModel(model.getName());
			} else {
				schema.setQualifiedType(clazz.getSimpleName().toLowerCase());
			}
		} else {
			ApiDocumentationNode node = new ApiDocumentationNode(clazz, descriptor.getName(), schemas, models);
			node.construct();
			schema.setQualifiedType(node.getValue().getId());
		}
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	public class ApiDocumentationNodePath extends Node<ApiDocumentationNode, ApiDocumentationNodePath, Model>.NodePath {

		public ApiDocumentationNodePath(List<ApiDocumentationNode> path) {
			super(path);
		}
	}
}
