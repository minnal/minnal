/**
 * 
 */
package org.minnal.examples.petclinic.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.NotEmpty;
import org.minnal.jpa.entity.BaseDomain;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author ganeshs
 * 
 */
@Entity
@Table(name = "visits")
@Access(AccessType.FIELD)
public class Visit extends BaseDomain {

	private Timestamp date;

	@NotEmpty
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name="petId")
	@JsonBackReference("visits")
	private Pet pet;

	/**
	 * @return the date
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Timestamp date) {
		this.date = date;
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
	 * @return the pet
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * @param pet the pet to set
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}
}
