/**
 * 
 */
package org.minnal.instrument.entity;

import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * @author ganeshs
 *
 */
@Entity
public class DummyModel {

	private Long id;
	
	private String code;
	
	@ElementCollection
	private List<Address> addresses; 
	
	@OneToMany
	private Set<DummyModel> children;
	
	@OneToMany
	private List<DummyModel> siblings;
	
	@ManyToOne
	private DummyModel parent;
	
	@OneToOne
	private DummyModel spouse;

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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	public String readCode() {
		return code;
	}

	/**
	 * @return the children
	 */
	public Set<DummyModel> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(Set<DummyModel> children) {
		this.children = children;
	}

	/**
	 * @return the parent
	 */
	public DummyModel getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(DummyModel parent) {
		this.parent = parent;
	}

	/**
	 * @return the siblings
	 */
	public List<DummyModel> getSiblings() {
		return siblings;
	}

	/**
	 * @param siblings the siblings to set
	 */
	public void setSiblings(List<DummyModel> siblings) {
		this.siblings = siblings;
	}
	
	public void dummyAction() {
	}
	
	public void dummyAction(String param1, Long param2) {
	}

	/**
	 * @return the spouse
	 */
	public DummyModel getSpouse() {
		return spouse;
	}

	/**
	 * @param spouse the spouse to set
	 */
	public void setSpouse(DummyModel spouse) {
		this.spouse = spouse;
	}
	
	/**
	 * @return the addresses
	 */
	public List<Address> getAddresses() {
		return addresses;
	}

	/**
	 * @param addresses the addresses to set
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	@Embeddable
	public static class Address {
		private String addressLine1;

		/**
		 * @return the addressLine1
		 */
		public String getAddressLine1() {
			return addressLine1;
		}

		/**
		 * @param addressLine1 the addressLine1 to set
		 */
		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}
	}
}
