package no.mil.fnse.core.model;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="controller")
public class SDNController {

	@JsonIgnore
	private int id;

	@JsonProperty("ip")
	String ipAddress;

	@JsonProperty("id")
	int entityId;
	
	@JsonProperty("interval")
	private int helloInterval;
	

	@JsonIgnore
	Collection<Peer> peers;

	public SDNController(){
		
	}
	
	
	
	// -------------------------------------------------------------------------
    // HashCode and equals
    // -------------------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
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
		SDNController other = (SDNController) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		return true;
	}

	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------

	@Id
	@GeneratedValue
	@Column(name = "CONTROLLER_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic(optional = false)
	@Column(name = "CONTROLLER_IP", unique = true, nullable = false)
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@Column(name = "ENTITY_ID")
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	
	@Column(name = "HELLO_INTERVAL")
	public int getHelloInterval() {
		return helloInterval;
	}
	public void setHelloInterval(int helloInterval) {
		this.helloInterval = helloInterval;
	}
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "controller")
	public Collection<Peer> getPeers() {
		return peers;
	}
	public void setPeers(Collection<Peer> peers) {
		this.peers = peers;
	}
	

}
