package no.mil.fnse.core.model;

import java.net.InetAddress;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.mil.fnse.core.model.networkElement.Router;

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
	Collection<Router> networkElements;
	
	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Basic(optional = false)
	@Column(name = "discoverymulticastgroup")
	public InetAddress getDiscoveryMulticastGroup() {
		return discoveryMulticastGroup;
	}
	public void setDiscoveryMulticastGroup(InetAddress multicastGroup) {
		this.discoveryMulticastGroup = multicastGroup;
	}
	
	@Column(name = "helloport")
	public int getHelloPort() {
		return helloPort;
	}
	public void setHelloPort(int helloPort) {
		this.helloPort = helloPort;
	}
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
	public SDNController getNationalController() {
		return nationalController;
	}
	public void setNationalController(SDNController nationalController) {
		this.nationalController = nationalController;
	}
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
	public Collection<Router> getNetworkElements() {
		return networkElements;
	}
	public void setNetworkElements(Collection<Router> networkElements) {
		this.networkElements = networkElements;
	}
}
