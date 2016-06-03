package no.mil.fnse.service;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.SouthboundException;
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
	public void init(){
		try {
			for (int lastOctett = 1; lastOctett < 255; lastOctett = lastOctett + 4) {
				String ifName = "tunnel1" + lastOctett + "0";
				String description = "IOP tunnel unused";
				InterfaceAddress ip = new InterfaceAddress(InetAddress.getByName(systemConfiguration.getNationalController().getEntityId()
						+ ".1.0." + lastOctett),
				"255.255.255.252");
				
				grePoolMaster.add(new NetworkInterface(ifName, description, ip));

				grePoolSlave.add(new NetworkInterface("tunnel2"+lastOctett+"0",description));
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
			Peer neighbor = defaultreposervice.getPeerByIp(InetAddress.getByName(localIp), InetAddress.getByName(remoteIp));
			Router router;
			NetworkInterface tunnel;
			if(neighbor == null){
				System.out.println("havent discovered this peer yet?!! We can basically send the hello message in this message as well...");
				router = defaultreposervice.getRouterByLocalIp(InetAddress.getByName(localIp));
				tunnel = null;
			}else{
				router = neighbor.getRouter();
				tunnel = neighbor.getTunnelInterface();
			}
			
			local.setBgp(router.getGlobalConfiguration().getBgpConfig());
			local.setMsdp(router.getGlobalConfiguration().getMsdpConfig());
			local.setTunnel(tunnel);
			return local;
		
		} catch (UnknownHostException e) {
			logger.error("Local or remote address in get request is not valid:  " +e);
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
			System.out.println("Configuring peer: " + neighbor.getId());
			vtyRouterDAO.setRouter(neighbor.getRouter());
			try {
				vtyRouterDAO.configureStaticRoute(InetAddress.getByName(neighbor.getController().getIpAddress()), "255.255.255.255",
						neighbor.getRemoteInterfaceIp().toString().substring(1));
			} catch (SouthboundException e) {
				logger.error("Attached failed: " + e);
				e.printStackTrace();
			} catch (UnknownHostException e) {
				logger.error("Attached failed: Could not convert controller ip to inet address!!"+e);
				e.printStackTrace();
			}
			String url = neighbor.getController().getIpAddress().toString().trim().substring(1)+"/api/configuration/local"+neighbor.getRemoteInterfaceIp()+neighbor.getLocalInterfaceIp();
			LocalConfiguration local = restTemplate.getForObject(url, LocalConfiguration.class);
			logger.info("REMOTE REST RESPONSE " +local.getBgp()+local.getTunnel() );
			
			configureTunnel(neighbor);
			// VI MÅ OGSÅ HENTE ALL CONFIG FRA REMOTE PEER

			/// CONFIGURE THE PEER

			defaultreposervice.updatePeer(neighbor.getId(), neighbor.getDeadTime(), PeerStatus.CONFIGURED,
					neighbor.getTunnelInterface());
		}

	}
	
	private void configureTunnel(Peer neighbor){
		NetworkInterface tunnel;

		if (neighbor.getController().getEntityId() < systemConfiguration.getNationalController()
				.getEntityId()) {
			tunnel = grePoolMaster.removeFirst();
		} else {
			// "GET REQUEST TO PEER"
			tunnel = grePoolSlave.removeFirst();
		}
		tunnel.setDescription("Tunnel_to:" + neighbor.getController().getEntityId());
		tunnel.setRouter(neighbor.getRouter());
		neighbor.setTunnelInterface(tunnel);
		defaultreposervice.updatePeer(neighbor.getId(), neighbor.getDeadTime(), neighbor.getStatus(), tunnel);
		
		vtyRouterDAO.configureTunnel(neighbor);
	}
	
	private void configureGlobal(){
		
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
			while (DiscoveryServiceImpl.deadQueue.isEmpty()) {
			}

			Peer deadNeighbor = DiscoveryServiceImpl.deadQueue.removeFirst();
			vtyRouterDAO.removeTunnel(deadNeighbor);
//			vtyRouterDAO.removeBgpPeer(deadNeighbor.getRouter().getGlobalConfiguration().getBgpConfig().getAsn(),deadNeighbor.getBgpPeer());

			defaultreposervice.delPeer(deadNeighbor.getId());
		}

	}
	


}
