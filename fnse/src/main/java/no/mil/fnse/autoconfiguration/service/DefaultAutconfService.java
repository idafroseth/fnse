package no.mil.fnse.autoconfiguration.service;

import java.net.InetAddress;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.mil.fnse.autoconfiguration.model.values.SystemWideConfiguration;
import no.mil.fnse.configuration.AutoconfConfiguration;
import no.mil.fnse.configuration.DatabaseInitialization;
import no.mil.fnse.core.model.DnsConfig;
import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SipConfig;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.TunnelInterface;
import no.mil.fnse.core.model.values.PeerStatus;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.SouthboundException;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;
import no.mil.fnse.discovery.service.DiscoveryServiceImpl;

@Component("defaultAutoconfService")
public class DefaultAutconfService implements AutoconfigurationService {

	@Autowired
	RepositoryService defaultreposervice;

	@Autowired
	RouterSouthboundDAO vtyRouterDAO;

	static Logger logger = Logger.getLogger(AutoconfigurationService.class);

	boolean isListeningPeers = false;
	boolean deadPeerListenerStarted = false;

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

		NetworkInterface networkif = defaultreposervice
				.getNetworkInterfaceByAddress(defaultreposervice.getInterfaceAddressByIp(local));
		if (networkif == null) {
			logger.error("Could not find network interface of local Ip: " + local);
			return new GlobalConfiguration();
		}
		return defaultreposervice.getRouterByNetworkInterface(networkif.getId()).getGlobalConfiguration();
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

	/*
	 * This method has to 1) Configure routing from controller to remote
	 * controller 2) decide the gre tunnel address 3) fetch the configuration
	 * from remote peer 4) configure the router 5) Update the database
	 */
	@Scheduled(initialDelay = 30 * 1000, fixedDelay = Long.MAX_VALUE)
	public void configureNewPeers() {
		if (isListeningPeers) {
			return;
		}

		logger.info("******* Autoconf Starting to listen for new peers!!");
		while (true) {
			isListeningPeers = true;
			while (DiscoveryServiceImpl.configurationQueue.isEmpty()) {
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Peer neighbor = DiscoveryServiceImpl.configurationQueue.removeFirst();
			System.out.println("Configuring peer: " + neighbor.getId());
			vtyRouterDAO.setRouter(neighbor.getRouter());
			try {
				vtyRouterDAO.configureStaticRoute(neighbor.getRemoteInterfaceIp(), "255.255.255.255",
						neighbor.getLocalInterfaceIp());
			} catch (SouthboundException e) {
				logger.error("Attached failed: " + e);
				e.printStackTrace();
			}

			NetworkInterface tunnel;

			if (neighbor.getController().getEntityId() < DatabaseInitialization.CONFIGURATION.getNationalController()
					.getEntityId()) {
				tunnel = AutoconfConfiguration.grePool.getFirst();
				tunnel.setDescription("Tunnel_to:" + neighbor.getController().getEntityId());

			} else {
				// "GET REQUEST TO PEER"
				tunnel = new NetworkInterface();
			}
			neighbor.setTunnelInterface(tunnel);

			// VI MÅ OGSÅ HENTE ALL CONFIG FRA REMOTE PEER

			/// CONFIGURE THE PEER

			defaultreposervice.updatePeer(neighbor.getId(), neighbor.getDeadTime(), PeerStatus.CONFIGURED,
					neighbor.getTunnelInterface());
		}

	}

	/*
	 * 1) This method has to delete all configuration of the peer. 2) Put the
	 * network interface back in the database 3) Remove the peer from the
	 * database
	 */
	@Scheduled(initialDelay = 30 * 1000, fixedDelay = Long.MAX_VALUE)
	public void removeDeadPeers() {
		if(deadPeerListenerStarted){
			return;
		}
		System.out.println("******* Autoconf Starting to listen for deadPeers!!");
		while (true) {
			deadPeerListenerStarted=true;
			while (DiscoveryServiceImpl.deadQueue.isEmpty()) {
			}

			Peer deadNeighbor = DiscoveryServiceImpl.deadQueue.removeFirst();

			// DELETE THE CONFIGURATION FOR THIS PEER

			defaultreposervice.delPeer(deadNeighbor.getId());
		}

	}

}
