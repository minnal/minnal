/**
 * 
 */
package org.minnal.examples.petclinic.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

import org.minnal.instrument.entity.Searchable;
import org.minnal.jpa.entity.BaseDomain;

/**
 * @author ganeshs
 *
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public class NamedEntity extends BaseDomain {

	@Searchable
	private String name;

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
}
