package no.mil.fnse.repository;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.model.Controller;

public interface ControllerDAO {
	
	/**
	 * Persists a Controller. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given Controller object and
     * returned.
	 * @param controller
	 * @return
	 */
	int saveController(Controller controller);
	
	/**
	 * Returns a Controller identified by the ID
	 * @param id
	 * @return the Controller or null if it does not exists
	 */
	Controller getController(int id);
	
	/**
	 * Returns the Controller with the IP
	 * @param ip
	 * @return the Controller or null if it does not exists
	 */
	Controller getControllerByIp(InetAddress ip);
	
	/**
	 * Returns all the Controllers in the database
	 * @return
	 */
	Collection<Controller> getAllControllers();
	
	/**
	 * Deletes a controller
	 * @param controller the controller to delte
	 */
	void delController(Controller controller);

}
