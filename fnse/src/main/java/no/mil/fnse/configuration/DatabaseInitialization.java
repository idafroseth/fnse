package no.mil.fnse.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.core.model.SystemConfiguration;
import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.SouthboundException;
import no.mil.fnse.core.service.TelnetCommunication;
import no.mil.fnse.core.southbound.VtyRouterDAO;

@Component("databaseInitialization")
public class DatabaseInitialization {

	@Autowired
	RepositoryService defaultreposervice;

	@Autowired
	VtyRouterDAO vtyRouterDAO;

	@Autowired
	Status configurationStatus;

	static Logger logger = Logger.getLogger(DatabaseInitialization.class);

	public static SystemConfiguration CONFIGURATION;

	/**
	 * Read the config.json file and configuring the basic values as Hello
	 * interval,
	 */
	@Scheduled(initialDelay = 1, fixedRate = Long.MAX_VALUE)
	public void configure() {
		if (!Status.databaseIsConfigured) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				CONFIGURATION = mapper.readValue(new File(System.getProperty("user.dir") + "/config.json"),
						SystemConfiguration.class);

				setRoutersAsNational(CONFIGURATION.getNetworkElements());

				defaultreposervice.addSystemConfiguration(CONFIGURATION);

				fetchConfigurationFromNetworkElement();
				Status.databaseIsConfigured = true;
				System.out.println("DATABASE READY: " + configurationStatus.getDatabaseIsConfigured());

			} catch (IOException e) {
				logger.error("Attached failed: " + e);
			}
		}

	}

	private void setRoutersAsNational(Collection<Router> routers) {
		for (Router router : routers) {
			router.setNational(true);
		}
	}

	// private void persistRouters(Collection<Router> routers) {
	// for (Router router : routers) {
	// router.setNational(true);
	// router.setId(defaultrepoService.addRouter(router));
	// }
	//
	// }

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
			 * 0) get from network element and persist Interface config 1) get
			 * from network element and persist BGP config
			 */
			router.setId(defaultreposervice.addRouter(router));
			try {
				vtyRouterDAO.setRouter(router);
				
				Collection<NetworkInterface> neList = vtyRouterDAO.getNetworkInterfaces();
				
				for (NetworkInterface ne : neList) {
					ne.setRouter(router);
					defaultreposervice.addNetworkInterface(ne);
				}

				BgpConfig bgpConfig = vtyRouterDAO.getBgpConfig();
				bgpConfig.setEbgpHop(1);
				defaultreposervice.addBgpConfiguration(bgpConfig);

				/*
				 * 2) get and persist NTP config 3) get and persist MSDP config
				 * 4) Construct and persist globalconfig
				 */
				GlobalConfiguration global = new GlobalConfiguration();
				global.setName(router.getManagementIp() + "");
				global.setBgpConfig(bgpConfig);
				int globalConfigId = defaultreposervice.addGlobalConfiguration(global);

				// 5) Add global config to Router
				defaultreposervice.addGlobalConfigurationToRouter(globalConfigId, router.getId());
			} catch (SouthboundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
