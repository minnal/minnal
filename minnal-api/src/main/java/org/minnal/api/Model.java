/**
 * 
 */
package org.minnal.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scala.Option;
import scala.collection.JavaConversions;

import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.ModelProperty;

/**
 * @author ganeshs
 *
 */
public class Model {

	private String id;

	private String name;

	private String qualifiedType;

	private LinkedHashMap<String, ModelProperty> properties = new LinkedHashMap<String, ModelProperty>();

	private String description;

	private String baseModel;

	private String discriminator;

	private List<String> subTypes = new ArrayList<String>();
	
	private AllowableValues allowableValues;

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
	public void setProperties(LinkedHashMap<String, ModelProperty> properties) {
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
	
	public com.wordnik.swagger.model.Model toModel() {
		scala.collection.mutable.LinkedHashMap<String, ModelProperty> map = new scala.collection.mutable.LinkedHashMap<String, ModelProperty>();
		for (Entry<String, ModelProperty> entry : properties.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return new com.wordnik.swagger.model.Model(id, name, qualifiedType, map, 
				Option.apply(description), Option.apply(baseModel), Option.apply(discriminator), 
				JavaConversions.asScalaBuffer(subTypes).toList());
	}
}
