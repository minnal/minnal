/**
 * 
 */
package org.minnal.examples.petclinic.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author ganeshs
 *
 */
@Entity
@Table(name="specialties")
@Access(AccessType.FIELD)
public class Specialty extends NamedEntity {

}
