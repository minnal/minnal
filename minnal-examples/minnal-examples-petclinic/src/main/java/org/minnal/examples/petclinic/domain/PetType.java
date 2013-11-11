/**
 * 
 */
package org.minnal.examples.petclinic.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.minnal.instrument.entity.AggregateRoot;

/**
 * @author ganeshs
 *
 */
@Entity
@Table(name="types")
@Access(AccessType.FIELD)
@AggregateRoot
public class PetType extends NamedEntity {

}
