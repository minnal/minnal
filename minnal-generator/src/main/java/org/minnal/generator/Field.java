/**
 * 
 */
package org.minnal.generator;

/**
 * @author ganeshs
 *
 */
public class Field {

	public enum Type {
		STRING("String"), INTEGER("Integer"), LONG("Long"), SHORT("Short"), CHAR("Character"), DOUBLE("Double"), FLOAT("Float"), DATE("java.util.Date"), TIMESTAMP("java.sql.Timestamp"), BOOLEAN("Boolean");
		
		private String javaType;
		
		private Type(String javaType) {
			this.javaType = javaType;
		}

		/**
		 * @return the javaType
		 */
		public String getJavaType() {
			return javaType;
		}
	}
	
	private String name;
	
	private Type type;
	
	private boolean searchable;
	
	public Field(String field) {
		String[] parts = field.split(":");
		name = parts[0];
		if (parts.length > 1) {
			try {
				type = Type.valueOf(parts[1].toUpperCase());
			} catch (Exception e) {
				type = Type.STRING;
			}
		} else {
			type = Type.STRING;
		}
		
		if (parts.length > 2) {
			try {
				searchable = Boolean.valueOf(parts[2]);
			} catch (Exception e) {
			}
		}
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
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the searchable
	 */
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * @param searchable the searchable to set
	 */
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
}
