package no.mil.fnse.configuration;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.model.DiscoveryConfig;


@Configuration
@ComponentScan("no.mil.fnse")
@EnableScheduling
public class DiscoveryConfiguration {
	

	public static  DiscoveryConfig DISCOVERY_CONFIG = new DiscoveryConfig();
	public static String HELLO_MSG;
	public static MulticastSocket HELLO_SOCKET;
	public static DatagramPacket HELLO_PACKET;
	
	private final int TTL = 10;


	
	private boolean hasRun = false;

	/**
	 * Read the config.json file and configuring the basic values as Hello interval,  
	 */
	@Scheduled(fixedRate = 3600000)
	public void configure(){
		if(!hasRun){
			ObjectMapper mapper = new ObjectMapper();
			try {
				///src/main/resources/
				DISCOVERY_CONFIG = mapper.readValue(new File(System.getProperty("user.dir") +"/config.json"), DiscoveryConfig.class);
				HELLO_MSG = mapper.writeValueAsString(DISCOVERY_CONFIG.getNATIONAL_CONTROLLER());
				HELLO_SOCKET = new MulticastSocket(DISCOVERY_CONFIG.getPORT());
				HELLO_SOCKET.setTimeToLive(TTL);
				HELLO_SOCKET.joinGroup(DISCOVERY_CONFIG.getMULTICAST_GROUP());
				HELLO_PACKET = new DatagramPacket(HELLO_MSG.getBytes(), HELLO_MSG.getBytes().length, DISCOVERY_CONFIG.getMULTICAST_GROUP(), DISCOVERY_CONFIG.getPORT());
			       
			}  catch (IOException e) {
				e.printStackTrace();
			}
			hasRun = true;
		}
		
	}
}
