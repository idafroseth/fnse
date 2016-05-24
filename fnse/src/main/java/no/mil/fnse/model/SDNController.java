package no.mil.fnse.model;

import java.net.InetAddress;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="controller")
public class SDNController {


	@JsonProperty("ip")
	InetAddress ipAddress;
	
	@JsonProperty("id")
	int entityId;
	
	@JsonProperty("i")
	private int helloInterval;
	

	@JsonIgnore
	List<Peer> peers;

	public SDNController(){
		
	}
	
	@Id
	@Basic(optional = false)
	@Column(name = "CONTROLLER_IP", unique = true, nullable = false)
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
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "controller")
	public List<Peer> getPeers() {
		return peers;
	}
	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}
	

}
