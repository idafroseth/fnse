package no.mil.fnse.autoconfiguration.service;

import no.mil.fnse.core.model.DnsConfig;
import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SipConfig;
import no.mil.fnse.core.model.SystemWideConfiguration;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.TunnelInterface;

public interface AutoconfigurationService {

	/**
	 * Fetch the tunnel interface for the connection based the connection id. 
	 * @param localIp
	 * @param remoteIp
	 * @return the gre tunnel configuration
	 */
	TunnelInterface getTunnelInterface(String localIp, String remoteIp);
	
	/**
	 * set the tunnel interface for the connection based the connection id. 
	 * @param localIp
	 * @param remoteIp
	 * @return the gre tunnel configuration
	 */
	TunnelInterface setTunnelInterface(String localIp, String remoteIp, TunnelInterface tunnel);
	
	Peer getLocalConfiguration(String localIp, String remoteIp);
	
	/**
	 * Provides the global configuration for a peer based on the connection
	 * identity, identifies by the local and Remote Ip.
	 * 
	 * @param localIp
	 * @param remoteIp
	 * @return the global configuration including BGP, MSDP and NTP
	 */
	GlobalConfiguration getGlobalConfiguration(String localIp);
	
	SystemWideConfiguration getSystemWideConfiguration();
	

	SipConfig getSipConfig();
	
	
	NtpConfig getNtpConfig();
	
	
	DnsConfig getDnsConfig();
	
	
	SipConfig updateSipConfig(SipConfig config);
	
	
	NtpConfig updateNtpConfig(NtpConfig config);
	
	
	DnsConfig updateDnsConfig(DnsConfig config);
	
	

}
