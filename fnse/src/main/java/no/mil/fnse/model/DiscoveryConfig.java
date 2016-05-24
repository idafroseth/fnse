package no.mil.fnse.model;

import java.net.InetAddress;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.mil.fnse.southbound.model.Router;

public class DiscoveryConfig {
	
	
	@JsonProperty("multicast_ip")
	InetAddress MULTICAST_GROUP;
	
	@JsonProperty("port")
	int PORT;
	
	@JsonProperty("national_ctrl")
	SDNController NATIONAL_CONTROLLER;
	
	@JsonProperty("network_elements")
	List<Router> networkElements;
	
	public InetAddress getMULTICAST_GROUP() {
		return MULTICAST_GROUP;
	}
	public void setMULTICAST_GROUP(InetAddress mULTICAST_GROUP) {
		MULTICAST_GROUP = mULTICAST_GROUP;
	}
	public int getPORT() {
		return PORT;
	}
	public void setPORT(int pORT) {
		PORT = pORT;
	}
	public SDNController getNATIONAL_CONTROLLER() {
		return NATIONAL_CONTROLLER;
	}
	public void setNATIONAL_CONTROLLER(SDNController nATIONAL_CONTROLLER) {
		NATIONAL_CONTROLLER = nATIONAL_CONTROLLER;
	}
	public List<Router> getNetworkElements() {
		return networkElements;
	}
	public void setNetworkElements(List<Router> networkElements) {
		this.networkElements = networkElements;
	}
	
	
	

	
	
	

}
