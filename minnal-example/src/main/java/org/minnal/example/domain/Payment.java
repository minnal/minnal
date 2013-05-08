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

/**
 * @author ganeshs
 *
 */
@AggregateRoot
@Entity
public class Payment extends Model {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

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
}
