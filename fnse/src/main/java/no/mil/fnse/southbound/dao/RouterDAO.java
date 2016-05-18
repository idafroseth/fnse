package no.mil.fnse.southbound.dao;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import no.mil.fnse.model.Peer;
import no.mil.fnse.southbound.model.BGPPeer;

public interface RouterDAO {
	
	/**
	 * Configure a new Peer
	 * @param peer
	 * @return
	 */
	boolean addNeighbor(Peer neighbor);
	
	boolean deleteNeighbor(Peer neighbor);
	
	//boolean addBGPPeer(BGPConfig bgpConfig);
	
//	boolean deleteBGPPeer(BGPConfig bgpConfig);
	
//	boolean addMSDPPeer(MSDPConfig msdpConfig);
	
	List<Peer> getAllConfiguredPeer();
	
	//List<BGPConfig> getAllConfiguredBGPPeer();
	
	/**
	 * Query the networking element for the multicast routing table
	 * @return a list of IPaddresses of remote peer and interface of local peer
	 */
	InetAddress getIpMrouteSource(InetAddress remotePeer);
	
	/**
	 * show ip bgp summary
	 * @return
	 */
	List<BGPPeer> getBGPNeighbors();
	

}
