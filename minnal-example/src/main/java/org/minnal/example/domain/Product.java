/**
 * 
 */
package org.minnal.example.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.activejpa.entity.Model;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.Searchable;

/**
 * @author ganeshs
 *
 */
@AggregateRoot
@Entity
public class Product extends Model {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Searchable
	private String name;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
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
}
