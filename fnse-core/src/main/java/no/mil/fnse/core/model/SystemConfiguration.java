package no.mil.fnse.core.model;

import java.net.InetAddress;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SystemConfiguration {
	
	@JsonIgnore
	int id;
	
	@JsonProperty("multicast_ip")
	InetAddress discoveryMulticastGroup;
	
	@JsonProperty("port")
	int helloPort;
	
	@JsonProperty("national_ctrl")
	SDNController nationalController;
	
	@JsonProperty("network_elements")
	List<Router> networkElements;
	
	@Id
	@GeneratedValue
	@Column(name = "CONFIG_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Basic(optional = false)
	@Column(name = "HELLO_MCASTGROUP")
	public InetAddress getDiscoveryMulticastGroup() {
		return discoveryMulticastGroup;
	}
	public void setDiscoveryMulticastGroup(InetAddress multicastGroup) {
		this.discoveryMulticastGroup = multicastGroup;
	}
	
	@Column(name = "HELLO_PORT")
	public int getHelloPort() {
		return helloPort;
	}
	public void setHelloPort(int helloPort) {
		this.helloPort = helloPort;
	}
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CONTROLLER_ID")
	public SDNController getNationalController() {
		return nationalController;
	}
	public void setNationalController(SDNController nationalController) {
		this.nationalController = nationalController;
	}
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ROUTER_ID")
	public List<Router> getNetworkElements() {
		return networkElements;
	}
	public void setNetworkElements(List<Router> networkElements) {
		this.networkElements = networkElements;
	}
}
