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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.minnal.instrument.entity.AggregateRoot;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author ganeshs
 *
 */
@Entity
@Table(name="owners")
@Access(AccessType.FIELD)
@AggregateRoot
public class Owner extends Person {

    @NotEmpty
    private String address;

    @NotEmpty
    private String city;

    @NotEmpty
    private String telephone;

    @OneToMany(cascade=CascadeType.ALL)
    @JsonManagedReference("pets")
    private Set<Pet> pets = new HashSet<Pet>();
    
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return the pets
	 */
	public Set<Pet> getPets() {
		return pets;
	}

	/**
	 * @param pets the pets to set
	 */
	public void setPets(Set<Pet> pets) {
		this.pets = pets;
	}
	
	/**
	 * @param pet
	 */
	public void addPet(Pet pet) {
		pet.setOwner(this);
        pets.add(pet);
    }
}
