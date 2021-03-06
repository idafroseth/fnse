package no.mil.fnse.core.repository;


import java.util.Collection;

import no.mil.fnse.core.model.networkElement.BgpConfig;

public interface BgpConfigDAO {
	/**
	 * Persists a BgpConfig. An unique id is generated if the object is persisted
     * for the first time, and which is both set in the given BgpConfig object and
     * returned.
	 * @param bgpConfig
	 * @return
	 */
	int saveBgpConfig(BgpConfig bgpConfig);
	
	/**
	 * Returns a BgpConfig identified by the ID
	 * @param id
	 * @return the BgpConfig or null if it does not exists
	 */
	BgpConfig getBgpConfig(int id);
	
	/**
	 * Returns the BgpConfig with the routerId and asn
	 * @param ip
	 * @return the BgpConfig or null if it does not exists
	 */
	BgpConfig getBgpConfigByRouterIdAndASN(String RouterId, String asn);
	
	/**
	 * Returns all the BgpConfigs in the database
	 * @return the collection of all bgpConfigs or null no one exists
	 */
	Collection<BgpConfig> getAllBgpConfigs();
	
	/**
	 * Returns all the bgpConfigs in the database controlled by this controller
	 * @return collection of bgpConfigs controlled by this controller
	 */
	Collection<BgpConfig> getAllNationalBgpConfigs();
	
	
	/**
	 * Deletes a bgpConfig
	 * @param bgpConfig the bgpConfig to delete
	 */
	void delBgpConfig(BgpConfig bgpConfig);
}
