/**
 * 
 */
package org.minnal.api;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.minnal.api.ApiDocumentationNode.ApiDocumentationNodePath;
import org.minnal.api.ApiDocumentationNode.SwaggerModel;
import org.minnal.core.util.Node;
import org.minnal.utils.reflection.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import scala.collection.JavaConversions;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;
import com.wordnik.swagger.model.ModelRef;

/**
 * @author ganeshs
 *
 */
public class ApiDocumentationNode extends Node<ApiDocumentationNode, ApiDocumentationNodePath, SwaggerModel> {
	
	private Class<?> clazz;
	
	private String name;
	
	private PropertyDescriptor descriptor;
	
	private Map<Class<?>, List<String>> visitedNodes = new HashMap<Class<?>, List<String>>();
	
	private Map<String, SwaggerModel> schemas;
	
	private Map<String, SwaggerModel> models;
	
	private static final Logger logger = LoggerFactory.getLogger(ApiDocumentationNode.class);

	public ApiDocumentationNode(Class<?> clazz) {
		this(clazz, clazz.getSimpleName(), new HashMap<String, SwaggerModel>(), new HashMap<String, SwaggerModel>());
	}
	
	public ApiDocumentationNode(Class<?> clazz, String name, Map<String, SwaggerModel> schemas, Map<String, SwaggerModel> models) {
		super(new SwaggerModel());
		this.schemas = schemas;
		this.models = models;
		this.name = name;
		this.clazz = clazz;
		getValue().setId(clazz.getSimpleName());
		getValue().setQualifiedType(clazz.getSimpleName());
	}
	
	public ApiDocumentationNode(PropertyDescriptor descriptor, Map<String, SwaggerModel> schemas, Map<String, SwaggerModel> models) {
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
						ApiDocumentationNode child = new ApiDocumentationNode(clazz, clazz.getSimpleName(), new HashMap<String, SwaggerModel>(), models);
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
		Map<String, ModelProperty> properties = new HashMap<String, ModelProperty>();
		for (ApiDocumentationNode node : getChildren()) {
			properties.put(node.getValue().getId(), createModelProperty(node.getValue()));
		}
		getValue().setProperties(properties);
	}
	
	protected ModelProperty createModelProperty(SwaggerModel model) {
		return new ModelProperty(model.getQualifiedType(), model.getQualifiedType(), 0, true, Option.apply(model.getDescription()), 
				model.getAllowableValues(), Option.apply(model.getItems()));
	}
	
	public Map<String, Model> getModels() {
		Map<String, Model> models = new HashMap<String, Model>();
		for (Entry<String, SwaggerModel> entry : this.models.entrySet()) {
			SwaggerModel swaggerModel = entry.getValue();
			scala.collection.mutable.LinkedHashMap<String, ModelProperty> properties = 
					scala.collection.mutable.LinkedHashMap$.MODULE$.<String, ModelProperty>apply(JavaConversions.asScalaMap(swaggerModel.getProperties()).toSeq());
			Model model = new Model(swaggerModel.getId(), swaggerModel.getName(), swaggerModel.getQualifiedType(), properties, Option.apply(swaggerModel.getDescription()),
					Option.apply(swaggerModel.getBaseModel()), Option.apply(swaggerModel.getDiscriminator()), JavaConversions.asScalaBuffer(Arrays.asList(new String[0])).toList());
			models.put(entry.getKey(), model);
		}
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
		SwaggerModel model = null;
		SwaggerModel schema = getValue();
		
		clazz = PropertyUtil.getType(descriptor);
		if (PropertyUtil.isSimpleProperty(descriptor.getPropertyType())) {
			if (Enum.class.isAssignableFrom(descriptor.getPropertyType())) {
				schema.setQualifiedType("string");
				List<String> values = PropertyUtil.getEnumValues(descriptor);
				AllowableListValues allowableValues = new AllowableListValues(JavaConversions.asScalaBuffer(values).toList(), "enum");
				schema.setAllowableValues(allowableValues);
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
				
				ModelRef modelRef = new ModelRef(model.getQualifiedType(), Option.apply(model.getName()), Option.apply(model.getQualifiedType()));
				schema.setQualifiedType("Array");
				schema.setItems(modelRef);
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
	public class ApiDocumentationNodePath extends Node<ApiDocumentationNode, ApiDocumentationNodePath, SwaggerModel>.NodePath {

		public ApiDocumentationNodePath(List<ApiDocumentationNode> path) {
			super(path);
		}
	}
	
	public static class SwaggerModel {
		
		private String id;
		
		private String name;
		
		private String qualifiedType;
		
		private Map<String, ModelProperty> properties = new LinkedHashMap<String, ModelProperty>();
		
		private String description;
		
		private String baseModel;
		
		private String discriminator;
		
		private List<String> subTypes = new ArrayList<String>();
		
		private AllowableValues allowableValues;
		
		private ModelRef items;

		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the qualifiedType
		 */
		public String getQualifiedType() {
			return qualifiedType;
		}

		/**
		 * @param qualifiedType the qualifiedType to set
		 */
		public void setQualifiedType(String qualifiedType) {
			this.qualifiedType = qualifiedType;
		}

		/**
		 * @return the properties
		 */
		public Map<String, ModelProperty> getProperties() {
			return properties;
		}

		/**
		 * @param properties the properties to set
		 */
		public void setProperties(Map<String, ModelProperty> properties) {
			this.properties = properties;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * @return the baseModel
		 */
		public String getBaseModel() {
			return baseModel;
		}

		/**
		 * @param baseModel the baseModel to set
		 */
		public void setBaseModel(String baseModel) {
			this.baseModel = baseModel;
		}

		/**
		 * @return the discriminator
		 */
		public String getDiscriminator() {
			return discriminator;
		}

		/**
		 * @param discriminator the discriminator to set
		 */
		public void setDiscriminator(String discriminator) {
			this.discriminator = discriminator;
		}

		/**
		 * @return the subTypes
		 */
		public List<String> getSubTypes() {
			return subTypes;
		}

		/**
		 * @param subTypes the subTypes to set
		 */
		public void setSubTypes(List<String> subTypes) {
			this.subTypes = subTypes;
		}

		/**
		 * @return the allowableValues
		 */
		public AllowableValues getAllowableValues() {
			return allowableValues;
		}

		/**
		 * @param allowableValues the allowableValues to set
		 */
		public void setAllowableValues(AllowableValues allowableValues) {
			this.allowableValues = allowableValues;
		}

		/**
		 * @return the items
		 */
		public ModelRef getItems() {
			return items;
		}

		/**
		 * @param items the items to set
		 */
		public void setItems(ModelRef items) {
			this.items = items;
		}
		
	}
}
