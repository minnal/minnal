/**
 * 
 */
package org.minnal.instrument.entity;

import java.util.List;

import org.activejpa.entity.Filter;

/**
 * @author ganeshs
 *
 */
public class PaginatedResponse<T> {

	private Long total;
	
	private Integer count;
	
	private Integer perPage;
	
	private Integer page;
	
	private List<T> data;
	
	/**
	 * @param filter
	 * @param data
	 * @param total
	 */
	public PaginatedResponse(Filter filter, List<T> data, long total) {
		this.total = total;
		this.count = data.size();
		this.page = filter.getPageNo();
		this.perPage = filter.getPerPage();
		this.data = data;
	}

	/**
	 * @return the total
	 */
	public Long getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Long total) {
		this.total = total;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the perPage
	 */
	public Integer getPerPage() {
		return perPage;
	}

	/**
	 * @param perPage the perPage to set
	 */
	public void setPerPage(Integer perPage) {
		this.perPage = perPage;
	}

	/**
	 * @return the page
	 */
	public Integer getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(Integer page) {
		this.page = page;
	}

	/**
	 * @return the data
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<T> data) {
		this.data = data;
	}
}
