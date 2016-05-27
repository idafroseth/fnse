package no.mil.fnse.core.southbound;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.BgpConfig;
import no.mil.fnse.core.model.BgpPeer;
import no.mil.fnse.core.model.NetworkInterface;
import no.mil.fnse.core.model.Router;

public  interface RouterSouthboundDAO {

	
	BgpConfig getBgpConfig(Router router);
	
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
	
	/**
	 * Query the networking element for the multicast routing table
	 * @return a list of IPaddresses of remote peer and interface of local peer
	 */
	InetAddress getIpMrouteSource(Router router,String multicastGroup ,InetAddress remotePeer);
	
	/**
	 * show ip bgp summary
	 * @return
	 */
	Collection<BgpPeer> getBGPNeighbors(Router router);
	
	
	public InetAddress getSecondaryInterfaceIp(Router router,String interfaceName);
	
	Collection<NetworkInterface> getNetworkInterfaces(Router router);
	
}
