package no.mil.fnse.service;

import java.net.InetAddress;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.GlobalConfiguration;
import no.mil.fnse.core.model.TunnelInterface;
import no.mil.fnse.core.model.NetworkInterface;
import no.mil.fnse.core.service.RepositoryService;

@Component("defaultAutoconfSerivice")
public class DefaultAutconfService implements AutoconfigurationService {

	
	@Autowired
	RepositoryService defaultreposervice;
	
	static Logger logger = Logger.getLogger(AutoconfigurationService.class);

	@Override
	public TunnelInterface getTunnelInterface(String localIp, String remoteIp) {
		// TODO Auto-generated method stub
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
	
}
