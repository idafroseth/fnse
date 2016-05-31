package no.mil.fnse.core.repository;

import java.util.Collection;

import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;

public interface NetworkInterfaceDAO {
	/**
	 * Persists a NetworkInterface. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given NetworkInterface object and
     * returned.
	 * @param networkInterface
	 * @return
	 */
	int saveNetworkInterface(NetworkInterface networkInterface);
	
	/**
	 * Returns a NetworkInterface identified by the ID
	 * @param id
	 * @return the NetworkInterface or null if it does not exists
	 */
	NetworkInterface getNetworkInterface(int id);
	
	/**
	 * Returns the NetworkInterface with the IP
	 * @param ip
	 * @return the NetworkInterface or null if it does not exists
	 */
	NetworkInterface getNetworkInterfaceByAddress(InterfaceAddress interfaceAddress);
	
	/**
	 * Returns all the NetworkInterfaces in the database
	 * @return the collection of all networkInterfaces or null no one exists
	 */
	Collection<NetworkInterface> getAllNetworkInterfaces();
	
	
	
	/**
	 * Deletes a networkInterface
	 * @param networkInterface the networkInterface to delete
	 */
	void delNetworkInterface(NetworkInterface networkInterface);
	
	/**
	 * Updates a networkInterface
	 * @param time
	 * @param controller
	 */
	void updateNetworkInterface(NetworkInterface networkInterface);
}
