package no.mil.fnse.core.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.values.PeerStatus;

/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: Peer.java 29 2016-05-24 19:28:08Z idamfro $
 */
@Entity
@Table(name="peer")
public class Peer implements Serializable{

	private static final long serialVersionUID = 123412721573912497L;

	@JsonIgnore
	private int id;

	private String localInterfaceIp;
	
	private String remoteInterfaceIp;
	
	@JsonIgnore
	private Timestamp deadTime;
	
	@JsonIgnore
	private PeerStatus status;
	
	@JsonIgnore
	private SDNController controller;
	
	@JsonProperty("gre_tunnel")
	private NetworkInterface tunnelInterface;

	@JsonIgnore
	private Router router;
	
	private BgpConfig bgpPeer;
	
	public Peer(){
		
	}
	
	 // -------------------------------------------------------------------------
    // Equals and hashcode
    // -------------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((localInterfaceIp == null) ? 0 : localInterfaceIp.hashCode());
		result = prime * result + ((remoteInterfaceIp == null) ? 0 : remoteInterfaceIp.hashCode());
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
		Peer other = (Peer) obj;
		if (localInterfaceIp == null) {
			if (other.localInterfaceIp != null)
				return false;
		} else if (!localInterfaceIp.equals(other.localInterfaceIp))
			return false;
		if (remoteInterfaceIp == null) {
			if (other.remoteInterfaceIp != null)
				return false;
		} else if (!remoteInterfaceIp.equals(other.remoteInterfaceIp))
			return false;
		return true;
	}


    
	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------

    @Id
	@GeneratedValue
	@Column( unique = true, nullable = false)
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
	}


	@Column(name = "localip", nullable = false)
	public String getLocalInterfaceIp() {
		return localInterfaceIp;
	}
	public void setLocalInterfaceIp(String localInterfaceAddress) {
		this.localInterfaceIp = localInterfaceAddress;
	}
	
	@Column(name = "remoteip", nullable = false)
	public String getRemoteInterfaceIp() {
		return remoteInterfaceIp;
	}
	public void setRemoteInterfaceIp(String remoteInterfaceIp) {
		this.remoteInterfaceIp = remoteInterfaceIp;
	}
	
	@Column
	public Timestamp getDeadTime() {
		return deadTime;
	}
	public void setDeadTime(Timestamp timestamp) {
		this.deadTime = timestamp;
	}
	
	@Column
	public PeerStatus getStatus() {
		return status;
	}
	public void setStatus(PeerStatus status) {
		this.status = status;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	public SDNController getController() {
		return controller;
	}
	public void setController(SDNController controller) {
		this.controller = controller;
	}

	@OneToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn
	public NetworkInterface getTunnelInterface() {
		return tunnelInterface;
	}
	
	public void setTunnelInterface(NetworkInterface greTunnel) {
		this.tunnelInterface = greTunnel;
	}

	@OneToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn
	public Router getRouter() {
		return router;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	@OneToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn
	public BgpConfig getBgpPeer() {
		return bgpPeer;
	}

	public void setBgpPeer(BgpConfig bgpPeer) {
		this.bgpPeer = bgpPeer;
	}
	
	

}
