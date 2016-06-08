package no.mil.fnse.core.southbound;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.MsdpConfig;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.service.SouthboundException;

public  interface RouterSouthboundDAO {

	
	Router getRouter();
	
	void setRouter(Router router);
	

	void configureBgpPeer(String localAsn,String updateSource, BgpConfig bgpPeer) throws SouthboundException;
	void removeBgpPeer(String localAsn, BgpConfig bgpPeer) throws SouthboundException;
	void configureNtpPeer(NtpConfig ntpPeer) throws SouthboundException;
	void removeNtpPeer(NtpConfig ntpPeer) throws SouthboundException;
	void configureTunnel(Peer peer) throws SouthboundException;
	void removeTunnel(String interfaceName) throws SouthboundException;
	void configureMsdpPeer(MsdpConfig config, String loopbackinterface) throws SouthboundException;
	void removeMsdpPeer(MsdpConfig config) throws SouthboundException;
	
	
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
	InetAddress getIpMrouteSource(String multicastGroup ,String remotePeer) throws SouthboundException;
	
	/**
	 * show ip bgp summary
	 * @return
	 */
	Collection<BgpConfig> getBGPNeighbors() throws SouthboundException;
	
	
	InetAddress getSecondaryInterfaceIp(String interfaceName) throws SouthboundException;
	
	Collection<NetworkInterface> getNetworkInterfaces() throws SouthboundException;
	
	void configureStaticRoute(InetAddress ipNetwork, String netmask, String nextHop) throws SouthboundException;
	
	void removeStaticRoute(InetAddress ipNetwork, String netmask, String nextHop)  throws SouthboundException;
	
	
}
