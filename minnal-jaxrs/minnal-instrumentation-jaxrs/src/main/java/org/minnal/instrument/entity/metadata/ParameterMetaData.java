/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import org.minnal.instrument.metadata.MetaData;


/**
 * @author ganeshs
 *
 */
public class ParameterMetaData extends MetaData {

	private String fieldName;
	
	private Class<?> type;
	
	public ParameterMetaData(String name, String fieldName, Class<?> type) {
		super(name);
		this.fieldName= fieldName;
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
