package no.mil.fnse.autoconfiguration.service;

import java.net.InetAddress;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.DnsConfig;
import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SipConfig;
import no.mil.fnse.core.model.SystemWideConfiguration;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.TunnelInterface;
import no.mil.fnse.core.service.RepositoryService;

@Component("defaultAutoconfSerivice")
public class DefaultAutconfService implements AutoconfigurationService {

	
	@Autowired
	RepositoryService defaultreposervice;
	
	static Logger logger = Logger.getLogger(AutoconfigurationService.class);

	@Override
	public TunnelInterface getTunnelInterface(String localIp, String remoteIp) {
		logger.error("getTunnelInterface METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public GlobalConfiguration getGlobalConfiguration(String localIp) {
		InetAddress local;
		try {
			local = InetAddress.getByName(localIp);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("The recieved address is not valid");
			return null;
		}
		
		NetworkInterface networkif = defaultreposervice.getNetworkInterfaceByIp(local);
		if(networkif == null){
			logger.error("Could not find network interface of local Ip: "+ local);
			return new GlobalConfiguration();
		}
		return networkif.getRouter().getGlobalConfiguration();
	}

	@Override
	public TunnelInterface setTunnelInterface(String localIp, String remoteIp, TunnelInterface tunnel) {
		logger.error("setTunnelInterface METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public Peer getLocalConfiguration(String localIp, String remoteIp) {
		logger.error("getLocalConfiguration METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public SystemWideConfiguration getSystemWideConfiguration() {
		logger.error("getSystemWideConfiguration METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public SipConfig getSipConfig() {
		return null;
	}

	@Override
	public NtpConfig getNtpConfig() {
		logger.error("getNtpConfig METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public DnsConfig getDnsConfig() {
		logger.error("getDnsConfig METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public SipConfig updateSipConfig(SipConfig config) {
		logger.error("updateSipConfig METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public NtpConfig updateNtpConfig(NtpConfig config) {
		logger.error(" updateNtpConfig METHOD NOT IMPLEMENTED YET!!");
		return null;
	}

	@Override
	public DnsConfig updateDnsConfig(DnsConfig config) {
		logger.error("updateDnsConfig METHOD NOT IMPLEMENTED YET!!");
		return null;
	}
	
}
