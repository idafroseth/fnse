package no.mil.fnse.service;

import no.mil.fnse.model.Peer;

public interface DiscoveryService {


	void discoverdNeighbor(Peer neighbor);
	
	/**
	 * Add a remote neighbor by first checking if its controller exsist. If the
	 * controller doesn´t exsist it will first add the controller before it adds
	 * the peer.
	 * @param neighbor
	 */
	void addNeighbor(Peer neighbor);
}
