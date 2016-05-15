package no.mil.fnse.service;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import no.mil.fnse.model.Controller;
import no.mil.fnse.model.Peer;
import no.mil.fnse.model.values.PeerStatus;

public interface Discovery {

	/**
	 * Return a peer identified by the interface ids
	 * @return
	 */
	Peer getPeer(InetAddress localIp, InetAddress remoteIp);
	
	/**
	 * Get a list of all the existing peers
	 * @return
	 */
	List<Peer> getAllPeers();
	
	/**
	 * Fetch all peers that have deadtime earlier than current time
	 * @param currentTime 
	 * @return a list of dead peers
	 */
	List<Peer> getDeadPeers(Date currentTime);
	
	/**
	 * Configure the status of a peer
	 * @param peer
	 * @param status
	 */
	void setPeerStatus(String peerId, PeerStatus status);
	
	/**
	 * Check if controller exist in the database if not add the controller. 
	 * Check if the peer exists in the database, if not add the peer. 
	 * Add the peer to the list of peers in the controller.
	 * 
	 */
	void addPeerToController(String peerId, Controller ctrl);
	
	/**
	 * 1) Check if the controller of the peer exisits
	 * 
	 * @param peer
	 */
	int addPeer(Peer peer);
	
	
}
