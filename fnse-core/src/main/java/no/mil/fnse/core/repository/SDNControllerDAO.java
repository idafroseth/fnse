package no.mil.fnse.core.repository;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.SDNController;

public interface SDNControllerDAO {
	/**
	 * Persists a SDNController. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given SDNController object and
     * returned.
	 * @param controller
	 * @return
	 */
	int saveSDNController(SDNController controller);
	
	/**
	 * Returns a SDNController identified by the ID
	 * @param id
	 * @return the SDNController or null if it does not exists
	 */
	SDNController getSDNController(int id);
	
	/**
	 * Returns the SDNController with the IP
	 * @param ip
	 * @return the SDNController or null if it does not exists
	 */
	SDNController getSDNControllerByIp(InetAddress ip);
	
	/**
	 * Returns all the SDNControllers in the database
	 * @return
	 */
	Collection<SDNController> getAllSDNControllers();
	
	
	/**
	 * Deletes a controller
	 * @param controller the controller to delte
	 */
	void delSDNController(SDNController controller);
}
