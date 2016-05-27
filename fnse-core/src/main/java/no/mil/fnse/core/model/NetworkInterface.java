package no.mil.fnse.core.model;

import java.net.InetAddress;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: Router.java 29 2016-05-25 19:28:08Z idamfro $
 */
@Entity
@Table(name="networkinterface")
public class NetworkInterface {
	private int id;
	private InetAddress ipAddress;
	private InetAddress ipv6Address;
	private String description;
	private String interfaceName;
	
	private Router router;
	
	static Logger logger = Logger.getLogger(NetworkInterface.class);
	
	
	// -------------------------------------------------------------------------
    // Hashcode and equals
    // -------------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((interfaceName == null) ? 0 : interfaceName.hashCode());
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
		NetworkInterface other = (NetworkInterface) obj;
		if (interfaceName == null) {
			if (other.interfaceName != null)
				return false;
		} else if (!interfaceName.equals(other.interfaceName))
			return false;
		return true;
	}
	
	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	
    @Id
	@GeneratedValue
	@Column(name = "NETWORKINTERFACE_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "IF_NAME")
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String ifName) {
		this.interfaceName = ifName;
	}
	
	@Column(name="IP4_ADR")
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@Transient
	public InetAddress getIpv6Address() {
		return ipv6Address;
	}
	public void setIpv6Address(InetAddress ipv6Address) {
		this.ipv6Address = ipv6Address;
	}
	
	@Column(name = "NE_DESCRIPTION")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ROUTER_ID", nullable = false)
	public Router getRouter() {
		return router;
	}
	public void setRouter(Router router) {
		this.router = router;
	}

	
}
