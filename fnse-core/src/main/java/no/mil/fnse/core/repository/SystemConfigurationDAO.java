package no.mil.fnse.core.repository;

import java.util.Collection;

import no.mil.fnse.core.model.SystemConfiguration;

public interface SystemConfigurationDAO {
	/**
	 * Persists a SystemConfiguration. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given SystemConfiguration object and
     * returned.
	 * @param controller
	 * @return
	 */
	int saveSystemConfiguration(SystemConfiguration controller);
	
	/**
	 * Returns a SystemConfiguration identified by the ID
	 * @param id
	 * @return the SystemConfiguration or null if it does not exists
	 */
	SystemConfiguration getSystemConfiguration(int id);
	
	/**
	 * Returns all the SystemConfigurations in the database
	 * @return
	 */
	Collection<SystemConfiguration> getAllSystemConfigurations();
	
	
	/**
	 * Deletes a controller
	 * @param controller the controller to delte
	 */
	void delSystemConfiguration(SystemConfiguration controller);
	
}
