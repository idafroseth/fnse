package no.mil.fnse.configuration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.core.model.SystemConfiguration;
import no.mil.fnse.discovery.service.DiscoveryService;
import no.mil.fnse.discovery.service.DiscoveryServiceImpl;

@Configuration
@Transactional
public class DiscoveryConfiguration {

	static Logger logger = Logger.getLogger(DiscoveryConfiguration.class);
	
	
	public static String HELLO_MSG;
	public static MulticastSocket SERVER_SOCKET;
	public static MulticastSocket CLIENT_SOCKET;
	public static DatagramPacket HELLO_PACKET;

	private final int TTL = 10;

	public static boolean discoveryIsConfigured = false;
	
	@Autowired
	private DiscoveryService discoveryServiceImpl;
	


	/**
	 * Read the config.json file and configuring the basic values as Hello
	 * interval,
	 */
	@Scheduled(initialDelay = 5*1000, fixedRate = Long.MAX_VALUE)
	public void configure() {
		System.out.println("*****************TRYING TO CONFIGURE DISCOVERY SERVICE");
		while(!Status.databaseIsConfigured){
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("NOW CONFIGURING DISCOVERY SERVICE");
		if (!discoveryIsConfigured) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				discoveryIsConfigured = true;
				// Generate hello message from config
				SystemConfiguration config = DatabaseInitialization.CONFIGURATION;
				HELLO_MSG = mapper.writeValueAsString(config.getNationalController());
				// Setup a socket to listen and send packages to/from
				// The server sender port in the HELLO_PACKET should be the
				// same
				// as the listener
				CLIENT_SOCKET = new MulticastSocket(config.getHelloPort());
				SERVER_SOCKET = new MulticastSocket(0);
				SERVER_SOCKET.setTimeToLive(TTL);
				SERVER_SOCKET.setLoopbackMode(true);
				// Join group is only nessecary for the listener. Should be
				// sent
				// regulary..
				CLIENT_SOCKET.joinGroup(config.getDiscoveryMulticastGroup());
				HELLO_PACKET = new DatagramPacket(HELLO_MSG.getBytes(), HELLO_MSG.getBytes().length,
						config.getDiscoveryMulticastGroup(), config.getHelloPort());
				Status.helloSocketIsReady = true;
				discoveryServiceImpl.listenHello();

			} catch (IOException e) {
				logger.error("Attached failed: " + e);
			}
			discoveryIsConfigured = true;
			
		}

	}

}
