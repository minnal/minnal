/**
 * 
 */
package org.minnal.security.session;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.activejpa.entity.Model;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.minnal.core.serializer.Serializer;

import com.google.common.io.BaseEncoding;

/**
 * @author ganeshs
 *
 */
@Entity
@Table(name="sessions")
public class JpaSession extends Model implements Session {

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	private String data;
	
	private String serviceTicket;
	
	private Timestamp createdAt;
	
	public JpaSession() {
	}
	
	/**
	 * @param id
	 */
	public JpaSession(String id) {
		this.id = id;
		this.createdAt = new Timestamp(System.currentTimeMillis());
	}

	@Override
	@Id
	public String getId() {
		return id;
	}

	@Column(length=2000)
	String getData() {
		return data;
	}
	
	void setData(String data) {
		this.data = data;
		decodeData(data);
	}
	
	@Transient
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T) attributes.get(name);
	}

	public boolean containsAttribute(String name) {
		return attributes.containsKey(name);
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	public void addAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Timestamp timestamp) {
		createdAt = timestamp;
	}
	
	void encodeData() {
		ChannelBuffer buffer = Serializer.DEFAULT_JSON_SERIALIZER.serialize(attributes);
		data = BaseEncoding.base64().encode(buffer.array());
	}
	
	void decodeData(String data) {
		byte[] bytes = BaseEncoding.base64().decode(data);
		setAttributes(Serializer.DEFAULT_JSON_SERIALIZER.deserialize(ChannelBuffers.wrappedBuffer(bytes), Map.class));
	}
	
	public boolean hasExpired(long timeoutInSecs) {
		return new Timestamp(System.currentTimeMillis() - timeoutInSecs * 1000).after(createdAt);
	}
	
	/**
	 * @return the serviceTicket
	 */
	public String getServiceTicket() {
		return serviceTicket;
	}

	/**
	 * @param serviceTicket the serviceTicket to set
	 */
	public void setServiceTicket(String serviceTicket) {
		this.serviceTicket = serviceTicket;
	}

	@Override
	public void persist() {
		encodeData();
		super.persist();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		JpaSession other = (JpaSession) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
