package no.mil.fnse.core.model.networkElement;

import java.util.Collection;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: Router.java 29 2016-05-25 19:28:08Z idamfro $
 */
@Entity
@Table(name="msdpconfig")
public class MsdpConfig {
	
	private int id;
	
	private String peerAddress;
	
	@JsonIgnore
	private Collection<String> peer;

	@JsonIgnore
	static Logger logger = Logger.getLogger(MsdpConfig.class);

	// -------------------------------------------------------------------------
    // Hashcode And Equals
    // -------------------------------------------------------------------------
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((peerAddress == null) ? 0 : peerAddress.hashCode());
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
		MsdpConfig other = (MsdpConfig) obj;
		if (peerAddress == null) {
			if (other.peerAddress != null)
				return false;
		} else if (!peerAddress.equals(other.peerAddress))
			return false;
		return true;
	}

	// -------------------------------------------------------------------------
    // Collectionters and getters
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
	


	@Column(name="peer_address", nullable=false)
	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}


	@ElementCollection
	public Collection<String> getPeer() {
		return peer;
	}

	public void setPeer(Collection<String> peer) {
		this.peer = peer;
	}

}
