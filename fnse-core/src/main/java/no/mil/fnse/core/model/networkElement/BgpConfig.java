package no.mil.fnse.core.model.networkElement;

import java.net.InterfaceAddress;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: BgpConfig.java 29 2016-05-25 19:28:08Z idamfro $
 */
@Entity
@Table(name="bgpconfig")
public class BgpConfig {
	
	@JsonIgnore
	private int id;
	private String routerId;
	
	@JsonIgnore
	private Collection<BgpConfig> neighbors;
	
	private String asn;
	private Integer ebgpHop;
	
	@JsonIgnore
	private boolean national;
	
	@JsonIgnore
	private Collection<InterfaceAddress> addressFamiliIpv4;
	@JsonIgnore
	private Collection<InterfaceAddress> addressFamiliIpv6;
	
	public BgpConfig(){
		
	}

	// -------------------------------------------------------------------------
    // Hashcode and Equals
    // -------------------------------------------------------------------------
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((routerId == null) ? 0 : routerId.hashCode());
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
		BgpConfig other = (BgpConfig) obj;
		if (routerId == null) {
			if (other.routerId != null)
				return false;
		} else if (!routerId.equals(other.routerId))
			return false;
		return true;
	}

	// -------------------------------------------------------------------------
    // Collectionters and getters
    // -------------------------------------------------------------------------

    @Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(nullable=false)
	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	@OneToMany
	@JoinColumn
	public Collection<BgpConfig> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Collection<BgpConfig> neighbors) {
		this.neighbors = neighbors;
	}

	@Column
	public String getAsn() {
		return asn;
	}

	public void setAsn(String asn) {
		this.asn = asn;
	}

	@Transient
	public Collection<InterfaceAddress> getAddressFamiliIpv4() {
		return addressFamiliIpv4;
	}

	public void setAddressFamiliIpv4(Collection<InterfaceAddress> addressFamiliIpv4) {
		this.addressFamiliIpv4 = addressFamiliIpv4;
	}

	@Transient
	public Collection<InterfaceAddress> getAddressFamiliIpv6() {
		return addressFamiliIpv6;
	}

	public void setAddressFamiliIpv6(Collection<InterfaceAddress> addressFamiliIpv6) {
		this.addressFamiliIpv6 = addressFamiliIpv6;
	}

	@Column(name="ebgphop")
	public Integer getEbgpHop() {
		return ebgpHop;
	}

	public void setEbgpHop(Integer ebgpHop) {
		this.ebgpHop = ebgpHop;
	}
	
	@Column
	public boolean isNational() {
		return national;
	}

	public void setNational(boolean national) {
		this.national = national;
	}
	
	

}
