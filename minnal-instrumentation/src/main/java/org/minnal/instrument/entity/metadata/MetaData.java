/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.util.Comparator;

/**
 * @author ganeshs
 *
 */
public abstract class MetaData {

	private String name;
	
	/**
	 * @param name
	 */
	public MetaData(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MetaData other = (MetaData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public static class MetaDataComparator implements Comparator<MetaData> {

		public int compare(MetaData o1, MetaData o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}
