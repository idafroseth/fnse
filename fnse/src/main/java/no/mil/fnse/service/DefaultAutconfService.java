package no.mil.fnse.service;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import no.mil.fnse.autoconfiguration.model.values.LocalConfiguration;
import no.mil.fnse.autoconfiguration.model.values.SystemWideConfiguration;
import no.mil.fnse.configuration.SystemConfiguration;
import no.mil.fnse.core.model.DnsConfig;
import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SipConfig;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.networkElement.TunnelInterface;
import no.mil.fnse.core.model.values.PeerStatus;
import no.mil.fnse.core.service.ExternalCommunication;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.SouthboundException;
import no.mil.fnse.core.service.TelnetCommunication;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;

@Service("autoconfigurationService")
public class DefaultAutconfService implements AutoconfigurationService {

	@Autowired
	RepositoryService defaultreposervice;

	@Autowired
	RouterSouthboundDAO vtyRouterDAO;

	@Autowired
	SystemConfiguration systemConfiguration;

	@Autowired
	RestTemplate restTemplate;

	static Logger logger = Logger.getLogger(AutoconfigurationService.class);

	private LinkedList<NetworkInterface> grePoolMaster = new LinkedList<NetworkInterface>();

	private LinkedList<NetworkInterface> grePoolSlave = new LinkedList<NetworkInterface>();

	@PostConstruct
	public void init() {
		try {
			for (int lastOctett = 1; lastOctett < 255; lastOctett = lastOctett + 4) {
				String ifName = "tunnel1" + lastOctett + "0";
				String description = "IOP tunnel unused";
				InterfaceAddress ip = new InterfaceAddress(
						InetAddress.getByName(
								systemConfiguration.getNationalController().getEntityId() + ".255.0." + lastOctett),
						"255.255.255.252");
				grePoolMaster.add(new NetworkInterface(ifName, description, ip));

				grePoolSlave.add(new NetworkInterface("tunnel2" + lastOctett + "0", description));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

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
	public LocalConfiguration getLocalConfiguration(String localIp, String remoteIp) {

		LocalConfiguration local = new LocalConfiguration();
		try {
			logger.info("Trying to get the peer by ip " + localIp + " and " + remoteIp);
			Peer neighbor = defaultreposervice.getPeerByIp(localIp, remoteIp);
			Router router;
			NetworkInterface tunnel;
			if (neighbor == null) {
				System.out.println(
						"havent discovered this peer yet?!! We can basically send the hello message in this message as well...");
				router = defaultreposervice.getRouterByLocalIp(InetAddress.getByName(localIp));
				if (router == null) {
					return local;
				}
				tunnel = null;
			} else {
				router = neighbor.getRouter();
				tunnel = neighbor.getTunnelInterface();
			}

			local.setBgp(router.getGlobalConfiguration().getBgpConfig());
			local.setMsdp(router.getGlobalConfiguration().getMsdpConfig());
			local.setTunnel(tunnel);
			return local;

		} catch (UnknownHostException e) {
			logger.error("Local or remote address in get request is not valid:  " + e);
			return null;
		}
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
	@Async
	public void configureNewPeers() {

		logger.info("******* Autoconf Starting to listen for new peers!!");
		while (true) {
			while (DiscoveryServiceImpl.configurationQueue.isEmpty()) {
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Peer neighbor = DiscoveryServiceImpl.configurationQueue.removeFirst();
			if (vtyRouterDAO.getRouter() == null) {
				vtyRouterDAO.setRouter(neighbor.getRouter());
			}

			if (neighbor.getTunnelInterface() == null) {
				setTunnel(neighbor);
			}

			neighbor = defaultreposervice.getPeerByIp(neighbor.getLocalInterfaceIp(), neighbor.getRemoteInterfaceIp());
			configureRouteToRemoteController(neighbor);

			LocalConfiguration remoteLocalConfig;
			try {
				String url = "http://" + neighbor.getController().getIpAddress().trim()
						+ ":8080/api/configuration/local/" + neighbor.getRemoteInterfaceIp() + "/"
						+ neighbor.getLocalInterfaceIp() + "/";

				logger.info("connecting to " + url);
				remoteLocalConfig = restTemplate.getForObject(url, LocalConfiguration.class);
				logger.info("REMOTE REST RESPONSE " + remoteLocalConfig.getBgp() + remoteLocalConfig.getTunnel());
			} catch (ResourceAccessException e) {
				logger.error("****No route to neighbor, addding it back into the queue");
				DiscoveryServiceImpl.configurationQueue.add(neighbor);
				continue;
			}
			// If the remote node is not ready the tunnel or bgp interface is
			// not set . Then add the node back to the queue and try again later
			if (remoteLocalConfig.getTunnel() == null || remoteLocalConfig.getBgp() == null) {
				DiscoveryServiceImpl.configurationQueue.add(neighbor);
				logger.info("Remote peer is not ready, adding the peer back to the queue");
				continue;
			}

			if (neighbor.getController().getEntityId() > systemConfiguration.getNationalController().getEntityId()) {
				try {
					logger.info("We are a slave node and have to configure the ip: "
							+ neighbor.getController().getEntityId());
					neighbor.getTunnelInterface()
							.setInterfaceAddress(getSlaveIp(remoteLocalConfig.getTunnel().getInterfaceAddress()));

					defaultreposervice.updateNetworkInterface(neighbor.getTunnelInterface());
				} catch (UnknownHostException e) {
					logger.error("Attached failed when trying to convert ip " + e);
					e.printStackTrace();
				}
			}

			int bgpId = defaultreposervice.addBgpConfiguration(remoteLocalConfig.getBgp());

			defaultreposervice.addBgpConfigToPeer(neighbor.getId(), bgpId);

			// VI MÅ OGSÅ HENTE ALL CONFIG FRA REMOTE PEER
			/// CONFIGURE THE PEER

			logger.info("###Trying to configure the tunnel!");
			try {
				vtyRouterDAO.configureTunnel(neighbor);
				vtyRouterDAO.configureBgpPeer(neighbor.getRouter().getGlobalConfiguration().getBgpConfig().getAsn(),
						"loopback" + systemConfiguration.getNationalController().getEntityId(),
						remoteLocalConfig.getBgp());

				vtyRouterDAO.configureStaticRoute(
						InetAddress.getByName(remoteLocalConfig.getBgp().getRouterId().trim()), "255.255.255.255",
						neighbor.getTunnelInterface().getInterfaceName());
			} catch (UnknownHostException | SouthboundException e) {
				logger.error("Attached failed: " + e);
				e.printStackTrace();
			}
			neighbor.setStatus(PeerStatus.CONFIGURED);

			defaultreposervice.updatePeer(neighbor);
		}

	}

	private boolean configureRouteToRemoteController(Peer neighbor) {
		try {
			vtyRouterDAO.configureStaticRoute(InetAddress.getByName(neighbor.getController().getIpAddress()),
					"255.255.255.255", neighbor.getRemoteInterfaceIp());
			return true;
		} catch (SouthboundException e) {
			logger.error("Attached failed: " + e);
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			logger.error("Attached failed: Could not convert controller ip to inet address!!" + e);
			e.printStackTrace();
			return false;
		}
	}

	private InterfaceAddress getSlaveIp(InterfaceAddress ipAdr) throws UnknownHostException {
		byte[] ip = ipAdr.getIp().getAddress();
		ip[3] += 1;
		ipAdr.setIp(InetAddress.getByAddress(ip));
		return ipAdr;
	}

	private void setTunnel(Peer neighbor) {
		NetworkInterface tunnel;

		if (neighbor.getController().getEntityId() < systemConfiguration.getNationalController().getEntityId()) {
			tunnel = grePoolMaster.removeFirst();
			int id = defaultreposervice.addInterfaceAddress(tunnel.getInterfaceAddress());
			// defaultreposervice.addInterfaceAddressToInterface);
		} else {
			// "GET REQUEST TO PEER"
			tunnel = grePoolSlave.removeFirst();
		}
		System.out.println("The neighbor ID IS:" + neighbor.getId());
		tunnel.setDescription("Tunnel_to:" + neighbor.getController().getEntityId());
		tunnel.setRouter(neighbor.getRouter());
		int tunnelId = defaultreposervice.addNetworkInterface(tunnel);
		defaultreposervice.addTunnelToNeighbor(neighbor.getId(), tunnelId);
		logger.info("Added tunnel to neighbor : " + neighbor.getId() + tunnel.getId());

	}

	private void configureGlobal() {

	}

	/*
	 * 1) This method has to delete all configuration of the peer. 2) Put the
	 * network interface back in the database 3) Remove the peer from the
	 * database
	 */
	@Async
	@Override
	public void removeDeadPeers() {

		System.out.println("******* Autoconf Starting to listen for deadPeers!!");

		while (true) {
			logger.debug("***********STARTING TO CHECK FOR DEAD PEERS!!!");

			Collection<Peer> deadPeers = defaultreposervice.getAllDeadPeers(new Timestamp(System.currentTimeMillis()));
			for (Peer deadNeighbor : deadPeers) {
				try {
					if (vtyRouterDAO.getRouter() == null) {
						vtyRouterDAO.setRouter(deadNeighbor.getRouter());
						if (deadNeighbor.getRouter() == null) {
							logger.error("The dead neighbor has not been assigned a ROUTER!!");
							continue;
						}
					}

					logger.info("Removing tunnel");
					vtyRouterDAO.removeTunnel(deadNeighbor.getTunnelInterface().getInterfaceName());
					logger.info("Removing bgp config");
					vtyRouterDAO.removeBgpPeer(
							deadNeighbor.getRouter().getGlobalConfiguration().getBgpConfig().getAsn(),
							deadNeighbor.getBgpPeer());

					logger.info("Removing static route to bgp peer");
					vtyRouterDAO.removeStaticRoute(
							InetAddress.getByName(deadNeighbor.getBgpPeer().getRouterId().trim()), "255.255.255.255",
							deadNeighbor.getRemoteInterfaceIp());
					logger.info("Removing static route to controller");
					vtyRouterDAO.removeStaticRoute(InetAddress.getByName(deadNeighbor.getController().getIpAddress()),
							"255.255.255.255", deadNeighbor.getRemoteInterfaceIp());
				} catch (UnknownHostException | SouthboundException e) {
					logger.error("Error occured when config to dead peer: " + e);
				}
				defaultreposervice.delPeer(deadNeighbor.getId());
			}
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				logger.error("Attached failed" + e);
				e.printStackTrace();
			}
		}

		// ALTERNATIVE APPROACH
		// while (true) {
		//
		//
		//
		// while (DiscoveryServiceImpl.deadQueue.isEmpty()) {
		// }
		// logger.info("Removing dead peer from queue");
		// Peer deadNeighbor = DiscoveryServiceImpl.deadQueue.removeFirst();
		// try {
		//
		// logger.info("Removing tunnel");
		// vtyRouterDAO.removeTunnel(deadNeighbor);
		// logger.info("Removing bgp config");
		// vtyRouterDAO.removeBgpPeer(systemConfiguration.getNationalRouters().get(0).getGlobalConfiguration()
		// .getBgpConfig().getAsn(), deadNeighbor.getBgpPeer());
		//
		// logger.info("Removing static route to bgp peer");
		// vtyRouterDAO.removeStaticRoute(InetAddress.getByName(deadNeighbor.getBgpPeer().getRouterId().trim()),
		// "255.255.255.255", deadNeighbor.getRemoteInterfaceIp());
		// logger.info("Removing static route to controller");
		// vtyRouterDAO.removeStaticRoute(InetAddress.getByName(deadNeighbor.getController().getIpAddress()),
		// "255.255.255.255", deadNeighbor.getRemoteInterfaceIp());
		// } catch (UnknownHostException | SouthboundException e) {
		// logger.error("Error occured when removing a route: " + e);
		// }
		// defaultreposervice.delPeer(deadNeighbor.getId());
		//
		// System.out.println("PEER SHOULD BE REMOVED FROM THE DATABASE");
		// }

	}

}
