package no.mil.fnse.configuration;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	SystemConfiguration systemConfiguration;

	static Logger logger = Logger.getLogger(DatabaseInitialization.class);

	/**
	 * Read the config.json file and configuring the basic values as Hello
	 * interval,
	 */
	public void configure() {
		System.out.println("initializing the database");

		setRoutersAsNational(systemConfiguration.getNationalRouters());

		connectToNetworkElements();
	
		fetchConfigurationFromNetworkElement();

		System.out.println("DATABASE IS READY");

	}

	private void setRoutersAsNational(Collection<Router> routers) {
		for (Router router : routers) {
			router.setNational(true);
		}
	}

	private void connectToNetworkElements() {

		for (Router router : systemConfiguration.getNationalRouters()) {
			router.setVty(new TelnetCommunication());
			router.openVty();
		}

	}
	
	private void persistRouter(Router router){
		logger.info("Persisting router: " + router.getManagementIp());
		router.setId(defaultreposervice.addRouter(router));
		
	}

	private void fetchConfigurationFromNetworkElement() {


		for (Router router : systemConfiguration.getNationalRouters()) {
			persistRouter(router);
			
			try {
				System.out.println("Connecting to router: " + router.getManagementIp());
				
				if(vtyRouterDAO.getRouter() != router){
					vtyRouterDAO.setRouter(router);
				}

				Collection<NetworkInterface> neList = vtyRouterDAO.getNetworkInterfaces();

				for (NetworkInterface ne : neList) {
					System.out.println("NetworkInterface: " + ne.getInterfaceName());
					
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
