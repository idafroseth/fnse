package no.mil.fnse.core.repository;

import java.util.Collection;

import no.mil.fnse.core.model.networkElement.GlobalConfiguration;

public interface GlobalConfigurationDAO {
	/**
	 * Persists a GlobalConfiguration. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given GlobalConfiguration object and
     * returned.
	 * @param globalConfiguration
	 * @return
	 */
	int saveGlobalConfiguration(GlobalConfiguration globalConfiguration);
	
	/**
	 * Returns a GlobalConfiguration identified by the ID
	 * @param id
	 * @return the GlobalConfiguration or null if it does not exists
	 */
	GlobalConfiguration getGlobalConfiguration(int id);
	
	/**
	 * Returns the GlobalConfiguration with the name
	 * @param ip
	 * @return the GlobalConfiguration or null if it does not exists
	 */
	GlobalConfiguration getGlobalConfigurationByName(String name);
	
	/**
	 * Returns all the GlobalConfigurations in the database
	 * @return the collection of all globalConfigurations or null no one exists
	 */
	Collection<GlobalConfiguration> getAllGlobalConfigurations();
	
	/**
	 * Returns all the globalConfigurations in the database controlled by this controller
	 * @return collection of globalConfigurations controlled by this controller
	 */
	Collection<GlobalConfiguration> getAllNationalGlobalConfigurations();
	
	
	/**
	 * Deletes a globalConfiguration
	 * @param globalConfiguration the globalConfiguration to delete
	 */
	void delGlobalConfiguration(GlobalConfiguration globalConfiguration);
	
	/**
	 * Updates a globalConfiguration with the correct time and controller
	 * @param time
	 * @param controller
	 */
	void updateGlobalConfiguration(GlobalConfiguration globalConfiguration);

}
