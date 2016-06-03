package no.mil.fnse.service;

import java.net.InetAddress;
import java.net.MulticastSocket;

import org.springframework.scheduling.annotation.Async;

public interface DiscoveryService {

	/**
	 * Periodically send Hello messages to remote peer. The HELLO message is a 
	 * multicast to a designated address and port. Advertising its controller IP address. 
	 */
	@Async
	void sendHello();
	
	/**
	 * Listen to incomming messages. When a message arrive it check wether the peer is configured and if it isnt it does that. 
	 */
	@Async
	void listenHello();
	
	/**
	 * Perodically checks if a peer is dead
	 * It is achieved by retriving a list of all the peers in the database.
	 * It compares the peers HELLO interval*3 + last seen timestamp with the current time.
	 * For nodes exceeding the dead interval it will be stamped with DEAD status.
	 */
	@Async
	void checkDeadPeer();
	
	
	
	void setGroup(InetAddress group);
	
	InetAddress getGroup();
	
	public MulticastSocket getServerSocket() ;

	public void setServerSocket(MulticastSocket serverSocket);
	

	public MulticastSocket getClientSocket();

	public void setClientSocket(MulticastSocket clientSocket);
	
	
}
