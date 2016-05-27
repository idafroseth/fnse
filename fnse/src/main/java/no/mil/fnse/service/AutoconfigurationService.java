package no.mil.fnse.service;

import no.mil.fnse.core.model.GlobalConfiguration;
import no.mil.fnse.core.model.TunnelInterface;

public interface AutoconfigurationService {

	/**
	 * Fetch the tunnel interface for the connection based the connection id. 
	 * @param localIp
	 * @param remoteIp
	 * @return the gre tunnel configuration
	 */
	TunnelInterface getTunnelInterface(String localIp, String remoteIp);
	
	/**
	 * Provides the global configuration for a peer based on the connection
	 * identity, identifies by the local and Remote Ip.
	 * 
	 * @param localIp
	 * @param remoteIp
	 * @return the global configuration including BGP, MSDP and NTP
	 */
	GlobalConfiguration getGlobalConfiguration(String localIp);


}
