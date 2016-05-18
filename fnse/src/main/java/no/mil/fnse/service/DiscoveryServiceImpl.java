package no.mil.fnse.service;

import java.io.IOException;
import java.net.DatagramPacket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.model.Controller;
import no.mil.fnse.model.Peer;
import no.mil.fnse.repository.HibernateControllerDAO;
import no.mil.fnse.configuration.DiscoveryConfiguration;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {
	static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);
	
	@Autowired
	HibernateControllerDAO hibernateControllerDAO;
	
	final int HELLO_INTERVAL = 30;
	boolean isListening = false;
    byte[] buf = new byte[256];
//    int listenerThreads=0;
    
    

	/**
	 * Sends a HELLO message every HELLO_INTERVAL
	 */
	@Scheduled(initialDelay=5*1000,fixedRate=30000)
	public void sendHello() {
        try {
			DiscoveryConfiguration.SERVER_SOCKET.send(DiscoveryConfiguration.HELLO_PACKET);
			logger.info("Sending HELLO");
		} catch (IOException re) {
			logger.error("Attached failed" + re);
		}		
	}

	/**
	 * DET ER ET PROBLEM AT DENNE STOPPER OPP ALLE TRÅDER NÅR DEN KJØRER 
	 * Vi trenger en pool med tråder som har en pool-size attribute
	 * WE NEED TO HAVE A SEPARATE SOCKET FOR THIS. WE ALSO NEED TO REGULARY SEND IGMP JOIN MESSAGES...
	 */
	@Scheduled(initialDelay=1*1000, fixedDelay = 3*1000)
	public void listenHello() {
		logger.info("Setting up listener port");
		if(!isListening){

            while (true) {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                try {
					DiscoveryConfiguration.CLIENT_SOCKET.receive(msgPacket);
	                String msg = new String(buf, 0, buf.length);
	                ObjectMapper mapper = new ObjectMapper();
	                Controller ctrl;
				
					ctrl = mapper.readValue(msg, Controller.class);
			
	                Peer neighbor = new Peer();
	                neighbor.setRemoteInterfaceIp( msgPacket.getAddress());
	                //REMEMBER TO CHANGE THIS!!  /// We have to find the intermediate interfaces
	                neighbor.setLocalInterfaceIp(null);
	                neighbor.setController(ctrl);
	//                if(isDiscovered(neighbor)){
	//                	//updateTimeStamp(neighbor);
	//                	System.out.print("Is already discovered: ");
	//                	
	//                }else{
	//                	neighbor.setStatus(PeerStatus.DISCOVERED);
	//                	discoveredNeighbors.add(neighbor.getRemoteInterfaceIp());
	//                	System.out.print("Neew peer: ");
	//                	
	//                }
	                logger.info("Discovered:"+ctrl.getEntityId() + ctrl.getIpAddress() + neighbor.getRemoteInterfaceIp() );
//	                listenerThreads--;
				} catch (JsonParseException re) {
					logger.error("Attached failed" + re);
				} catch (JsonMappingException re) {
					logger.error("Attached failed" + re);
				} catch (IOException re) {
					logger.error("Attached failed" + re);
				}
            }
		}
		
	}
	
	public void checkDeadPeer() {
		// TODO Auto-generated method stub
		/*
		 * Check database where current date time > timestamp and not marked as DEAD
		 * 
		 */
		
		
	}



}
