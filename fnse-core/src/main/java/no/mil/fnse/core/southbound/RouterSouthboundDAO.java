package no.mil.fnse.core.southbound;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.BgpPeer;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.service.SouthboundException;

public  interface RouterSouthboundDAO {

	
	Router getRouter();
	
	void setRouter(Router router);
	

	
//	/**
//	 * Configure a new Peer
//	 * @param peer
//	 * @return
//	 */
//	boolean addNeighbor(Peer neighbor);
//	
//	boolean deleteNeighbor(Peer neighbor);
	
	//boolean addBGPPeer(BGPConfig bgpConfig);
	
//	boolean deleteBGPPeer(BGPConfig bgpConfig);
	
//	boolean addMSDPPeer(MSDPConfig msdpConfig);
	
//	List<Peer> getAllConfiguredPeer();
	
	//List<BGPConfig> getAllConfiguredBGPPeer();
	
	BgpConfig getBgpConfig() throws SouthboundException;
	
	/**
	 * Query the networking element for the multicast routing table
	 * @return a list of IPaddresses of remote peer and interface of local peer
	 */
	InetAddress getIpMrouteSource(String multicastGroup ,InetAddress remotePeer) throws SouthboundException;
	
	/**
	 * show ip bgp summary
	 * @return
	 */
	Collection<BgpPeer> getBGPNeighbors() throws SouthboundException;
	
	
	InetAddress getSecondaryInterfaceIp(String interfaceName) throws SouthboundException;
	
	Collection<NetworkInterface> getNetworkInterfaces() throws SouthboundException;
	
	void configureStaticRoute(InetAddress ipNetwork, String netmask, InetAddress nextHop) throws SouthboundException;
	
	
	
}
