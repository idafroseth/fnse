package no.mil.fnse.configuration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.core.model.SystemConfiguration;

@Configuration
@ComponentScan("no.mil.fnse")
@Transactional
public class DiscoveryConfiguration {

	static Logger logger = Logger.getLogger(DiscoveryConfiguration.class);
	
	
	public static String HELLO_MSG;
	public static MulticastSocket SERVER_SOCKET;
	public static MulticastSocket CLIENT_SOCKET;
	public static DatagramPacket HELLO_PACKET;

	private final int TTL = 10;

	public static boolean discoveryIsConfigured = false;


	/**
	 * Read the config.json file and configuring the basic values as Hello
	 * interval,
	 */
	@Scheduled(fixedRate = 3600000)
	public void configure() {
		if (DatabaseInitialization.databaseReady) {
			if (!discoveryIsConfigured) {
				ObjectMapper mapper = new ObjectMapper();
				try {

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

					// Join group is only nessecary for the listener. Should be
					// sent
					// regulary..
					CLIENT_SOCKET.joinGroup(config.getDiscoveryMulticastGroup());
					HELLO_PACKET = new DatagramPacket(HELLO_MSG.getBytes(), HELLO_MSG.getBytes().length,
							config.getDiscoveryMulticastGroup(), config.getHelloPort());

				} catch (IOException e) {
					logger.error("Attached failed: " + e);
				}
				discoveryIsConfigured = true;
			}
		}

	}

}
