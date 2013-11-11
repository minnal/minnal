/**
 * 
 */
package org.minnal.examples.petclinic.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.NotEmpty;
import org.minnal.instrument.entity.Searchable;
import org.minnal.jpa.entity.BaseDomain;

/**
 * @author ganeshs
 *
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class Person extends BaseDomain {

	@Column
    @NotEmpty
    @Searchable
    private String firstName;

    @Column
    @NotEmpty
    @Searchable
    private String lastName;

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
