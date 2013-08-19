/**
 * 
 */
package org.minnal.example.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.activejpa.entity.Model;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.Searchable;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author ganeshs
 *
 */
@AggregateRoot
@Entity
@Table(name="Orders")
public class Order extends Model {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="orderId")
	@JsonManagedReference
	private Set<OrderItem> orderItems = new HashSet<OrderItem>();
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="orderId")
	private Set<Payment> payments;
	
	@Searchable
	private String customerEmail;

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
	 * @return the orderItems
	 */
	public Set<OrderItem> getOrderItems() {
		return orderItems;
	}

	/**
	 * @param orderItems the orderItems to set
	 */
	public void setOrderItems(Set<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * @return the payments
	 */
	public Set<Payment> getPayments() {
		return payments;
	}

	/**
	 * @param payments the payments to set
	 */
	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	
	public void addOrderItem(OrderItem orderItem) {
		orderItem.setOrder(this);
		this.orderItems.add(orderItem);
	}

}
