package no.mil.fnse.core.model.networkElement;

import java.net.InetAddress;

import org.apache.log4j.Logger;

public class TunnelInterface extends NetworkInterface{

	private InetAddress tunnelDestination;
	private InetAddress tunnelSource;
	
	private InetAddress remoteIpAddress;
	
	static Logger logger = Logger.getLogger(TunnelInterface.class);
	
	public TunnelInterface(){
		
	}


	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	
	public InetAddress getTunnelDestination() {
		return tunnelDestination;
	}

	public void setTunnelDestination(InetAddress tunnelDestination) {
		this.tunnelDestination = tunnelDestination;
	}

	public InetAddress getTunnelSource() {
		return tunnelSource;
	}

	public void setTunnelSource(InetAddress tunnelSource) {
		this.tunnelSource = tunnelSource;
	}

	public InetAddress getRemoteIpAddress() {
		return remoteIpAddress;
	}


	public void setRemoteIpAddress(InetAddress remoteIpAddress) {
		this.remoteIpAddress = remoteIpAddress;
	}
	
	

}
