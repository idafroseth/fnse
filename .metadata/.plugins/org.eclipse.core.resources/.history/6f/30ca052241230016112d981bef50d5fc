package no.mil.fnse.core.repository;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.Router;

public interface RouterDAO {
	/**
	 * Persists a Router. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given Router object and
     * returned.
	 * @param router
	 * @return
	 */
	int saveRouter(Router router);
	
	/**
	 * Returns a Router identified by the ID
	 * @param id
	 * @return the Router or null if it does not exists
	 */
	Router getRouter(int id);
	
	/**
	 * Returns the Router with the IP
	 * @param ip
	 * @return the Router or null if it does not exists
	 */
	Router getRouterByManagementIp(InetAddress mangementIp);
	
	/**
	 * Returns all the Routers in the database
	 * @return the collection of all routers or null no one exists
	 */
	Collection<Router> getAllRouters();
	
	/**
	 * Returns all the routers in the database controlled by this controller
	 * @return collection of routers controlled by this controller
	 */
	Collection<Router> getAllNationalRouters();
	
	
	/**
	 * Deletes a router
	 * @param router the router to delete
	 */
	void delRouter(Router router);
	
	/**
	 * Updates a router with the correct time and controller
	 * @param time
	 * @param controller
	 */
	void updateRouter(Router router);
}
