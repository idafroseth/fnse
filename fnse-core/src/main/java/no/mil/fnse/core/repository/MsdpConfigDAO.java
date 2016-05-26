package no.mil.fnse.core.repository;

import java.net.InetAddress;
import java.util.Collection;

import no.mil.fnse.core.model.MsdpConfig;

public interface MsdpConfigDAO {
	/**
	 * Persists a MsdpConfig. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given MsdpConfig object and
     * returned.
	 * @param msdpConfig
	 * @return
	 */
	int saveMsdpConfig(MsdpConfig msdpConfig);
	
	/**
	 * Returns a MsdpConfig identified by the ID
	 * @param id
	 * @return the MsdpConfig or null if it does not exists
	 */
	MsdpConfig getMsdpConfig(int id);
	
	/**
	 * Returns the MsdpConfig with the IP
	 * @param ip
	 * @return the MsdpConfig or null if it does not exists
	 */
	MsdpConfig getMsdpConfigByAddress(InetAddress ip);
	
	/**
	 * Returns all the MsdpConfigs in the database
	 * @return the collection of all msdpConfigs or null no one exists
	 */
	Collection<MsdpConfig> getAllMsdpConfigs();
	
	/**
	 * Deletes a msdpConfig
	 * @param msdpConfig the msdpConfig to delete
	 */
	void delMsdpConfig(MsdpConfig msdpConfig);
	
	/**
	 * Updates a msdpConfig with the correct time and controller
	 * @param time
	 * @param controller
	 */
	void updateMsdpConfig(MsdpConfig msdpConfig);
}
