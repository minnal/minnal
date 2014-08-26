/**
 *
 */
package org.minnal.examples.petclinic.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.minnal.jpa.entity.BaseDomain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author ganeshs
 */
@Entity
@Table(name = "pets")
@Access(AccessType.FIELD)
public class Pet extends BaseDomain {

    private Timestamp birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId", insertable = false, updatable = false)
    @JsonIgnore
    private PetType type;

    private Long typeId;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonBackReference("pets")
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference("visits")
    private Set<Visit> visits = new HashSet<Visit>();

    /**
     * @return the owner
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * @return the birthDate
     */
    public Timestamp getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Timestamp birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the type
     */
    public PetType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(PetType type) {
        this.typeId = type.getId();
        this.type = type;
    }

    /**
     * @return the visits
     */
    public Set<Visit> getVisits() {
        return visits;
    }

    /**
     * @param visits the visits to set
     */
    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    /**
     * @return the typeId
     */
    public Long getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
}
