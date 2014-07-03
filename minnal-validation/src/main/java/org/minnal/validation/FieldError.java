/**
 * 
 */
package org.minnal.validation;

/**
 * @author ganeshs
 *
 */
public class FieldError {

	private String field;
	
	private String message;
	
	private Object invalidValue;

	/**
	 * @param field
	 * @param message
	 * @param invalidValue
	 */
	public FieldError(String field, String message, Object invalidValue) {
		this.field = field;
		this.message = message;
		this.invalidValue = invalidValue;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the invalidValue
	 */
	public Object getInvalidValue() {
		return invalidValue;
	}

	/**
	 * @param invalidValue the invalidValue to set
	 */
	public void setInvalidValue(Object invalidValue) {
		this.invalidValue = invalidValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result
				+ ((invalidValue == null) ? 0 : invalidValue.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldError other = (FieldError) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (invalidValue == null) {
			if (other.invalidValue != null)
				return false;
		} else if (!invalidValue.equals(other.invalidValue))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
