/**
 * 
 */
package org.minnal.examples.oms.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.activejpa.entity.Model;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.Searchable;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author ganeshs
 *
 */
@AggregateRoot
@Entity
@Table(name="Orders")
@Secure(method=Method.GET, permissions={"view_order"})
public class Order extends Model {
	
	/**
	 * @author ganeshs
	 *
	 */
	public enum Status {
		created, cancelled
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="orderId")
	@JsonManagedReference
	private Set<OrderItem> orderItems = new HashSet<OrderItem>();
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="orderId")
	private Set<Payment> payments = new HashSet<Payment>();
	
	@Searchable
	private String customerEmail;
	
	@Searchable
	@Enumerated(EnumType.STRING)
	private Status status = Status.created;
	
	private String cancellationReason;

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
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * @return the cancellationReason
	 */
	public String getCancellationReason() {
		return cancellationReason;
	}

	/**
	 * @param cancellationReason the cancellationReason to set
	 */
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public void addOrderItem(OrderItem orderItem) {
		orderItem.setOrder(this);
		this.orderItems.add(orderItem);
	}
	
	// This method will expose the route /orders/{order_id}/cancel
	// Your payload should be a json structure with keys mapping to the name of the method arguments
	// In this scenario the payload would be {"reason": "some cancellation reason"}
	// Minnal will automatically call this method with the reason taken from payload
	@Action(value="cancel")
	public void cancel(String reason) {
		setStatus(Status.cancelled);
		setCancellationReason(reason);
	}
	
	// This method will expose the route /orders/{order_id}/order_items/{order_item_id}/cancel
	// Your payload should be a json structure with keys mapping to the name of the method arguments
	// In this scenario the payload would be {"reason": "some cancellation reason"}
	// Minnal will automatically call this method with the reason taken from payload
	@Action(value="cancel", path="orderItems")
	public void cancelOrderItem(OrderItem orderItem, String reason) {
		orderItem.cancel(reason);
	}

}
