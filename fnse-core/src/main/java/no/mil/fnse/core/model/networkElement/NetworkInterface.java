package no.mil.fnse.core.model.networkElement;

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
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: Router.java 29 2016-05-25 19:28:08Z idamfro $
 */
@Entity
@Table(name="networkinterface")
public class NetworkInterface {
	@JsonIgnore
	private int id;
	
	private InterfaceAddress interfaceAddress;
	
	private InterfaceAddress ipv6Address;
	@JsonIgnore
	private String description;
	@JsonIgnore
	private String interfaceName;
//	
	@JsonIgnore
	private Router router;
//	
	@JsonIgnore
	static Logger logger = Logger.getLogger(NetworkInterface.class);
	
	
	public NetworkInterface(){
		
	}
	
	public NetworkInterface(String name, String description){
		this.interfaceName = name;
		this.description = description;
	}
	
	public NetworkInterface(String name, String description, InterfaceAddress ifAdr){
		this.interfaceName = name;
		this.description = description;
		this.interfaceAddress = ifAdr;
	}
	
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
	@Column( unique = true, nullable = false)
    @GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name="interfacename")
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String ifName) {
		this.interfaceName = ifName;
	}
	
	@OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
	@JoinColumn
	public InterfaceAddress getInterfaceAddress() {
		return interfaceAddress;
	}
	
	public void setInterfaceAddress(InterfaceAddress interfaceAddress) {
		this.interfaceAddress = interfaceAddress;
	}
	
	@Transient
	public InterfaceAddress getIpv6Address() {
		return ipv6Address;
	}
	public void setIpv6Address(InterfaceAddress ipv6Address) {
		this.ipv6Address = ipv6Address;
	}
	
	@Column
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	public Router getRouter() {
		return router;
	}
	public void setRouter(Router router) {
		this.router = router;
	}
//
//	
}
