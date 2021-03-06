package no.mil.fnse.core.repository;

import java.util.Collection;

import no.mil.fnse.core.model.NtpConfig;

public interface NtpConfigDAO {
		/**
		 * Persists a NtpConfig. An unique id is generated if the object is persisted
	     * for the first time, and which is both set in the given NtpConfig object and
	     * returned.
		 * @param ntpConfig
		 * @return
		 */
		int saveNtpConfig(NtpConfig ntpConfig);
		
		/**
		 * Returns a NtpConfig identified by the ID
		 * @param id
		 * @return the NtpConfig or null if it does not exists
		 */
		NtpConfig getNtpConfig(int id);
		
		/**
		 * Returns the NtpConfig with the IP
		 * @param ip
		 * @return the NtpConfig or null if it does not exists
		 */
		NtpConfig getNtpConfigByAddress(String ip);
		
		/**
		 * Returns all the NtpConfigs in the database
		 * @return the collection of all ntpConfigs or null no one exists
		 */
		Collection<NtpConfig> getAllNtpConfigs();
		
		/**
		 * Returns all the ntpConfigs in the database controlled by this controller
		 * @return collection of ntpConfigs controlled by this controller
		 */
		Collection<NtpConfig> getAllNationalNtpConfigs();
		
		
		/**
		 * Deletes a ntpConfig
		 * @param ntpConfig the ntpConfig to delete
		 */
		void delNtpConfig(NtpConfig ntpConfig);
		
		/**
		 * Updates a ntpConfig with the correct time and controller
		 * @param time
		 * @param controller
		 */
		void updateNtpConfig(NtpConfig ntpConfig);
}
