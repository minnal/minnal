/**
 * 
 */
package org.minnal.jpa.entity;

import java.io.Serializable;

import javax.persistence.Entity;

import org.activejpa.entity.Model;

/**
 * @author ganeshs
 *
 */
@Entity
public class BaseDomain extends Model {

	@Override
	public Serializable getId() {
		return null;
	}
}
