package no.mil.fnse.core.model;

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

	private InetAddress localInterfaceIp;
	
	private InetAddress remoteInterfaceIp;
	
	private Timestamp deadTime;
	
	private PeerStatus status;
	
	private SDNController controller;

	
	public Peer(){
		
	}
	
	 // -------------------------------------------------------------------------
    // Equals and hashcode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return localInterfaceIp.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Peer) )
        {
            return false;
        }

        final Peer other = (Peer) o;

        return (localInterfaceIp+""+remoteInterfaceIp).equals( other.getLocalInterfaceIp() +"" + other.getRemoteInterfaceIp() );
    }
    
	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------

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
	public InetAddress getLocalInterfaceIp() {
		return localInterfaceIp;
	}
	public void setLocalInterfaceIp(InetAddress localInterfaceAddress) {
		this.localInterfaceIp = localInterfaceAddress;
	}
	
	@Column(name = "PEER_REMOTE_IP", nullable = false)
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
	@JoinColumn(name = "CONTROLLER_ID", nullable = false)
	public SDNController getController() {
		return controller;
	}
	public void setController(SDNController controller) {
		this.controller = controller;
	}
	
	

}
