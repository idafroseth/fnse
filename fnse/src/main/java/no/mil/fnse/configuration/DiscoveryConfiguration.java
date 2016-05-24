package no.mil.fnse.configuration;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.model.DiscoveryConfig;
import no.mil.fnse.southbound.model.Router;

@Configuration
@ComponentScan("no.mil.fnse")
public class DiscoveryConfiguration {

	static Logger logger = Logger.getLogger(DiscoveryConfiguration.class);

	public static DiscoveryConfig DISCOVERY_CONFIG = new DiscoveryConfig();
	public static String HELLO_MSG;
	public static MulticastSocket SERVER_SOCKET;
	public static MulticastSocket CLIENT_SOCKET;
	public static DatagramPacket HELLO_PACKET;

	private final int TTL = 10;

	private boolean hasRun = false;

	/**
	 * Read the config.json file and configuring the basic values as Hello
	 * interval,
	 */
	@Scheduled(fixedRate = 3600000)
	public void configure() {
		if (!hasRun) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				DISCOVERY_CONFIG = mapper.readValue(new File(System.getProperty("user.dir") + "/config.json"),
						DiscoveryConfig.class);
				// Generate hello message from config
				HELLO_MSG = mapper.writeValueAsString(DISCOVERY_CONFIG.getNATIONAL_CONTROLLER());
				// Setup a socket to listen and send packages to/from
				// The server sender port in the HELLO_PACKET should be the same
				// as the listener
				CLIENT_SOCKET = new MulticastSocket(DISCOVERY_CONFIG.getPORT());
				SERVER_SOCKET = new MulticastSocket(0);
				SERVER_SOCKET.setTimeToLive(TTL);

				// Join group is only nessecary for the listener. Should be sent
				// regulary..
				CLIENT_SOCKET.joinGroup(DISCOVERY_CONFIG.getMULTICAST_GROUP());
				HELLO_PACKET = new DatagramPacket(HELLO_MSG.getBytes(), HELLO_MSG.getBytes().length,
						DISCOVERY_CONFIG.getMULTICAST_GROUP(), DISCOVERY_CONFIG.getPORT());

				connectToNetworkElements();

			} catch (IOException e) {
				logger.error("Attached failed: " + e);
			}
			hasRun = true;
		}

	}

	private void connectToNetworkElements() {
		for (Router router : DISCOVERY_CONFIG.getNetworkElements()) {
				router.openVty();
				logger.info("Successful connection to NetworkElement - " + router.getManagementIp());
			
		}

	}

}
