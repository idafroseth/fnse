package no.mil.fnse.core.service;

import java.net.InetAddress;

public interface ExternalCommunication {
	/**
	 * Connect to a remote host
	 * @param mngAddress 
	 * @param username
	 * @param password
	 * @return
	 */
	boolean connect(InetAddress mngAddress, String username, String password);
	
	/**
	 * Send a command to a remote host
	 * @param command
	 * @return the response from the remote host
	 */
	String send(String command);
	
	/**
	 * Close the connection to the remote host
	 */
	void close();
	
	/**
	 * Check if the session is open
	 * @return
	 */
	boolean isOpen();
}
