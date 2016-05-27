package no.mil.fnse.core.model;

import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import no.mil.fnse.core.service.ExternalCommunication;

/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: Router.java 29 2016-05-25 19:28:08Z idamfro $
 */
@Entity
@Table(name="router")
public class Router {
	
	private int id;

	private InetAddress managementIp;
	
	private String username;

	private String password;
	
	private GlobalConfiguration globalConfiguration;

	private List<NetworkInterface> networkInterfaces;
	
	private boolean national;
	
	
	@JsonIgnore
	private ExternalCommunication vty;

	@JsonIgnore
	static Logger logger = Logger.getLogger(Router.class);

	public Router() {
	}
	
	public Router(InetAddress mngIp, String username, String pwd){
		this.managementIp=mngIp;
		this.username = username;
		this.password = pwd;
	}

	public boolean openVty() {
		if (managementIp != null && username != null && password != null) {
			vty.connect(managementIp, username, password);
			return true;
		} else {
			logger.error("username, password or management IP isnt set");
			return false;
		}
	}
	
	
	// -------------------------------------------------------------------------
    // Hashcode and Equals
    // -------------------------------------------------------------------------
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((managementIp == null) ? 0 : managementIp.hashCode());
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
		Router other = (Router) obj;
		if (managementIp == null) {
			if (other.managementIp != null)
				return false;
		} else if (!managementIp.equals(other.managementIp))
			return false;
		return true;
	}

	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	

    @Id
	@GeneratedValue
	@Column(name = "ROUTER_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "MANAGEMENT_IP", nullable = false)
	public InetAddress getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(InetAddress managementIp) {
		this.managementIp = managementIp;
	}

	@Transient
	public ExternalCommunication getVty() {
		return vty;
	}

	public void setVty(ExternalCommunication vty) {
		this.vty = vty;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "ROUTER_USERNAME")
	public String getUsername() {
		return this.username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "ROUTER_PASSWORD")
	public String getPassword() {
		return this.password;
	}
	
	@OneToOne
	@JoinColumn(name = "GLOBAL_CONFIG")
	public GlobalConfiguration getGlobalConfiguration() {
		return globalConfiguration;
	}

	public void setGlobalConfiguration(GlobalConfiguration globalConfiguration) {
		this.globalConfiguration = globalConfiguration;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "router")
	public List<NetworkInterface> getNetworkInterfaces() {
		return networkInterfaces;
	}

	public void setNetworkInterfaces(List<NetworkInterface> networkInterfaces) {
		this.networkInterfaces = networkInterfaces;
	}

	@Column(name="ROUTER_NATIONAL")
	public boolean isNational() {
		return national;
	}

	public void setNational(boolean national) {
		this.national = national;
	}

	
	

}
