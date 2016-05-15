package no.mil.fnse.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

import javax.persistence.Basic;
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
	
	private Date deadTime;
	
	private PeerStatus status;
	
	private Controller controller;
	
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CONTROLLER_ID", nullable = false)
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	@Column(name = "PEER_LOCAL_IP", nullable = false)
	@Id
	public InetAddress getLocalInterfaceIp() {
		return localInterfaceIp;
	}
	public void setLocalInterfaceIp(InetAddress localInterfaceAddress) {
		this.localInterfaceIp = localInterfaceAddress;
	}
	
	@Column(name = "PEER_LOCAL_IP", unique = false, nullable = false)
	@Id
	public InetAddress getRemoteInterfaceIp() {
		return remoteInterfaceIp;
	}
	public void setRemoteInterfaceIp(InetAddress remoteInterfaceIp) {
		this.remoteInterfaceIp = remoteInterfaceIp;
	}
	
	@Column(name = "PEER_DEAD_TIME")
	public Date getDeadTime() {
		return deadTime;
	}
	public void setDeadTime(Date deadTime) {
		this.deadTime = deadTime;
	}
	
	@Column(name = "PEER_STATUS")
	public PeerStatus getStatus() {
		return status;
	}
	public void setStatus(PeerStatus status) {
		this.status = status;
	}
	
	

}
