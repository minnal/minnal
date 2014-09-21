/**
 * 
 */
package org.minnal.jpa.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.activejpa.entity.Model;

/**
 * @author ganeshs
 *
 */
@MappedSuperclass
@Access(AccessType.PROPERTY)
public class BaseDomain extends Model {
	
	private Long id;

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
