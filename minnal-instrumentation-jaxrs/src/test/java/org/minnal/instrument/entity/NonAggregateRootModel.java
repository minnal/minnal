/**
 * 
 */
package org.minnal.instrument.entity;

import javax.persistence.Entity;

/**
 * @author ganeshs
 *
 */
@Entity
public class NonAggregateRootModel {

	private Long id;
	
	private String code;

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

	@Action(value="customAction")
	public void dummyAction() {
	}
	
	@Action(value="customActionWithParams")
	public void dummyAction(String param1, Long param2) {
	}
}
