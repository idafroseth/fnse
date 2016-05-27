package no.mil.fnse.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.core.model.SystemConfiguration;
import no.mil.fnse.core.model.BgpConfig;
import no.mil.fnse.core.model.GlobalConfiguration;
import no.mil.fnse.core.model.NetworkInterface;
import no.mil.fnse.core.model.Router;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.TelnetCommunication;
import no.mil.fnse.core.southbound.VtyRouterDAO;

@Configuration
@Transactional
public class DatabaseInitialization {

	@Autowired
	RepositoryService defaultreposervice;
	
	@Autowired
	VtyRouterDAO vtyRouterDAO;
	
	public static boolean databaseReady = false;
	
	static Logger logger = Logger.getLogger(DatabaseInitialization.class);
	
	public static SystemConfiguration CONFIGURATION;


	
	/**
	 * Read the config.json file and configuring the basic values as Hello
	 * interval,
	 */
	@Scheduled(fixedRate = 3600000)
	public void configure() {
		if (!databaseReady) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				SystemConfiguration systemconfig = mapper.readValue(new File(System.getProperty("user.dir") + "/config.json"),
						SystemConfiguration.class);
				// Generate hello message from config

				setRoutersAsNational(systemconfig.getNetworkElements());

				defaultreposervice.addSystemConfiguration(systemconfig);
			
				fetchConfigurationFromNetworkElement();

			} catch (IOException e) {
				logger.error("Attached failed: " + e);
			}
			databaseReady = true;
		}

	}
	
	private void setRoutersAsNational(Collection<Router> routers){
		for(Router router : routers){
			router.setNational(true);
		}
	}

//	private void persistRouters(Collection<Router> routers) {
//		for (Router router : routers) {
//			router.setNational(true);
//			router.setId(defaultrepoService.addRouter(router));
//		}
//
//	}

	private void connectToNetworkElements() {
		
		for (Router router : CONFIGURATION.getNetworkElements()) {
			router.setVty(new TelnetCommunication());
			router.openVty();
			logger.info("Successful connection to NetworkElement - " + router.getManagementIp());
		}

	}

	private void fetchConfigurationFromNetworkElement() {

		connectToNetworkElements();
		
		for (Router router : CONFIGURATION.getNetworkElements()) {
			/*
			 * 0) get from network element and persist Interface config 
			 * 1) get
			 * from network element and persist BGP config
			 */
			Collection<NetworkInterface> neList = vtyRouterDAO.getNetworkInterfaces(router);
			for(NetworkInterface ne : neList){
				ne.setRouter(router);
				defaultreposervice.addNetworkInterface(ne);
			}
			
			BgpConfig bgpConfig = vtyRouterDAO.getBgpConfig(router);
			bgpConfig.setEbgpHop(1);
			defaultreposervice.addBgpConfiguration(bgpConfig);

			/*
			 * 2) get and persist NTP config 
			 * 3) get and persist MSDP config
			 * 4) Construct and persist globalconfig
			 */
			GlobalConfiguration global = new GlobalConfiguration();
			global.setName(router.getManagementIp() + "");
			global.setBgpConfig(bgpConfig);
			int globalConfigId = defaultreposervice.addGlobalConfiguration(global);
			
			 // 5) Add global config to Router
			defaultreposervice.addGlobalConfigurationToRouter(globalConfigId, router.getId());

		}

	}
}
