/**
 * 
 */
package org.minnal.examples.petclinic.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.minnal.instrument.entity.AggregateRoot;

/**
 * @author ganeshs
 *
 */
@Entity
@Table(name="vets")
@Access(AccessType.FIELD)
@AggregateRoot
public class Vet extends Person {

	@ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="vet_specialties", joinColumns=@JoinColumn(name="vetId"),
            inverseJoinColumns = @JoinColumn(name="specialtyId"))
    private Set<Specialty> specialties = new HashSet<Specialty>();
	
	/**
	 * @return the specialties
	 */
	public Set<Specialty> getSpecialties() {
		return specialties;
	}

	/**
	 * @param specialties the specialties to set
	 */
	public void setSpecialties(Set<Specialty> specialties) {
		this.specialties = specialties;
	}
}
