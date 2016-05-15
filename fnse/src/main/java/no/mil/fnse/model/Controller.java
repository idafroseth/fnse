package no.mil.fnse.model;

import java.net.InetAddress;
import java.util.List;

import javax.persistence.Basic;
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
public class Controller {
	
	@JsonIgnore
	int id;

	@JsonProperty("ip")
	InetAddress ipAddress;
	
	@JsonProperty("id")
	int entityId;
	
	@JsonProperty("i")
	private int helloInterval;
	

	@JsonIgnore
	List<Peer> peers;

	public Controller(){
		
	}
	
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
	@Column(name = "CONTROLLER_IP")
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress) {
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
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "controller")
	public List<Peer> getPeers() {
		return peers;
	}
	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}
	

}
