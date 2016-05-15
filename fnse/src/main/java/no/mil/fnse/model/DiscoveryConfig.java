package no.mil.fnse.model;

import java.net.InetAddress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiscoveryConfig {
	
	
	@JsonProperty("multicast_ip")
	InetAddress MULTICAST_GROUP;
	
	@JsonProperty("port")
	int PORT;
	
	@JsonProperty("national_ctrl")
	Controller NATIONAL_CONTROLLER;
	
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
	public Controller getNATIONAL_CONTROLLER() {
		return NATIONAL_CONTROLLER;
	}
	public void setNATIONAL_CONTROLLER(Controller nATIONAL_CONTROLLER) {
		NATIONAL_CONTROLLER = nATIONAL_CONTROLLER;
	}
	

	
	
	

}
