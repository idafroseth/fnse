package no.mil.fnse.core.repository;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;

import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;

public interface PeerDAO {
	/**
	 * Persists a Peer. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given Peer object and
     * returned.
	 * @param peer
	 * @return
	 */
	int savePeer(Peer peer);
	
	/**
	 * Returns a Peer identified by the ID
	 * @param id
	 * @return the Peer or null if it does not exists
	 */
	Peer getPeer(int id);
	
	/**
	 * Returns the Peer with the IP
	 * @param ip
	 * @return the Peer or null if it does not exists
	 */
	Peer getPeerByIp(String local, String remote);
	
	/**
	 * Returns all the Peers in the database
	 * @return the collection of all peers or null no one exists
	 */
	Collection<Peer> getAllPeers();
	
	/**
	 * Returns all the Peers which are assosicaited with a local interface address
	 * @param localIp
	 * @return the collection of peers or null if it does not exists
	 */
	Collection<Peer> getAllPeersWithLocalIp(String localIp);
	
	/**
	 * Returns a list of all the peers connected a specific remote neighbor. 
	 * @param remoteIp
	 * @return the collection of peers or null if it does not exists
	 */
	Collection<Peer> getAllPeersWithRemoteIp(String remoteIp);
	
	/**
	 * Returns a list of all the peers controlled by a specific controller 
	 * @param  controller
	 * @return the collection of peers or null if it does not exists
	 */
	Collection<Peer> getAllPeersWithSDNController(SDNController controller);
	
	/**
	 * Returns all the SDNControllers in the database which is dead
	 * @return
	 */
	Collection<Peer> getAllDeadPeers(Timestamp time);
	
	
	/**
	 * Deletes a peer
	 * @param peerId the peer to delete
	 */
	void delPeer(Peer peerId);
	
	/**
	 * Updates a peer with the correct time and controller
	 * @param time
	 * @param controller
	 */
	void updatePeer(Peer peer);
	
	
}
