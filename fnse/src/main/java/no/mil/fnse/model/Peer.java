package no.mil.fnse.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.mil.fnse.model.values.PeerStatus;

@Entity
@Table(name="peer")
public class Peer implements Serializable{
	
	@JsonIgnore
	int id;
	
	private InetAddress localInterfaceIp;
	
	private InetAddress remoteInterfaceIp;
	
	private Timestamp deadTime;
	
	private PeerStatus status;
	
	private SDNController controller;
	
	public Peer(){
		
	}
	
	@Id
	@GeneratedValue
	@Column(name = "PEER_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "PEER_LOCAL_IP", nullable = false)
	@Id
	public InetAddress getLocalInterfaceIp() {
		return localInterfaceIp;
	}
	public void setLocalInterfaceIp(InetAddress localInterfaceAddress) {
		this.localInterfaceIp = localInterfaceAddress;
	}
	
	@Column(name = "PEER_REMOTE_IP", nullable = false)
	@Id
	public InetAddress getRemoteInterfaceIp() {
		return remoteInterfaceIp;
	}
	public void setRemoteInterfaceIp(InetAddress remoteInterfaceIp) {
		this.remoteInterfaceIp = remoteInterfaceIp;
	}
	
	@Column(name = "PEER_DEAD_TIME")
	public Timestamp getDeadTime() {
		return deadTime;
	}
	public void setDeadTime(Timestamp timestamp) {
		this.deadTime = timestamp;
	}
	
	@Column(name = "PEER_STATUS")
	public PeerStatus getStatus() {
		return status;
	}
	public void setStatus(PeerStatus status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CONTROLLER_IP", nullable = false)
	public SDNController getController() {
		return controller;
	}

	public void setController(SDNController controller) {
		this.controller = controller;
	}
	
	

}
