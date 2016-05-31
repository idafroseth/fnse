package no.mil.fnse.core.repository;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.networkElement.InterfaceAddress;


public interface InterfaceAddressDAO {
	/**
	 * Persists a InterfaceAddress. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given InterfaceAddress object and
     * returned.
	 * @param interfaceAddress
	 * @return
	 */
	int saveInterfaceAddress(InterfaceAddress interfaceAddress);
	
	/**
	 * Returns a InterfaceAddress identified by the ID
	 * @param id
	 * @return the InterfaceAddress or null if it does not exists
	 */
	InterfaceAddress getInterfaceAddress(int id);
	
	/**
	 * Returns the InterfaceAddress with the ip
	 * @param ip
	 * @return the InterfaceAddress or null if it does not exists
	 */
	InterfaceAddress getInterfaceAddressByIp(InetAddress name);
	
	/**
	 * Returns all the InterfaceAddresss in the database
	 * @return the collection of all interfaceAddresss or null no one exists
	 */
	Collection<InterfaceAddress> getAllInterfaceAddresss();

	/**
	 * Deletes a interfaceAddress
	 * @param interfaceAddress the interfaceAddress to delete
	 */
	void delInterfaceAddress(InterfaceAddress interfaceAddress);
	
	/**
	 * Updates a interfaceAddress with the correct time and controller
	 * @param time
	 * @param controller
	 */
	void updateInterfaceAddress(InterfaceAddress interfaceAddress);

}
