/**
 * 
 */
package org.minnal.security.auth.cas;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.activejpa.entity.Model;

/**
 * @author ganeshs
 *
 */
@Entity
@Table(name="cas_pgt_ious")
public class CasPgtIou extends Model {
	
	private Long id;
	
	private String pgt;
	
	private String iou;
	
	public CasPgtIou() {
	}

	/**
	 * @param pgt
	 * @param iou
	 */
	public CasPgtIou(String pgt, String iou) {
		this.pgt = pgt;
		this.iou = iou;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	 * @return the pgt
	 */
	public String getPgt() {
		return pgt;
	}

	/**
	 * @param pgt the pgt to set
	 */
	public void setPgt(String pgt) {
		this.pgt = pgt;
	}

	/**
	 * @return the iou
	 */
	public String getIou() {
		return iou;
	}

	/**
	 * @param iou the iou to set
	 */
	public void setIou(String iou) {
		this.iou = iou;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iou == null) ? 0 : iou.hashCode());
		result = prime * result + ((pgt == null) ? 0 : pgt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CasPgtIou other = (CasPgtIou) obj;
		if (iou == null) {
			if (other.iou != null)
				return false;
		} else if (!iou.equals(other.iou))
			return false;
		if (pgt == null) {
			if (other.pgt != null)
				return false;
		} else if (!pgt.equals(other.pgt))
			return false;
		return true;
	}

}
