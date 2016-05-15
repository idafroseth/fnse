package no.mil.fnse.service;

import java.io.IOException;
import java.net.DatagramPacket;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.model.Controller;
import no.mil.fnse.model.Peer;
import no.mil.fnse.configuration.DiscoveryConfiguration;

@EnableScheduling
@Service
public class DiscoveryServiceImpl implements DiscoveryService {
	final int HELLO_INTERVAL = 30;
	boolean listenToHelloStarted = false;

	/**
	 * Sends a HELLO message every HELLO_INTERVAL
	 */
	@Scheduled(initialDelay=5*1000,fixedRate=HELLO_INTERVAL*1000)
	public void sendHello() {
        try {
			DiscoveryConfiguration.HELLO_SOCKET.send(DiscoveryConfiguration.HELLO_PACKET);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Scheduled(initialDelay=6*1000, fixedRate = 3600000)
	public void listenHello() {
		if(!listenToHelloStarted){
	        byte[] buf = new byte[256];
            while (true) {
                // Receive the information and print it.
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                try {
					DiscoveryConfiguration.HELLO_SOCKET.receive(msgPacket);
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
	                System.out.println(""+ctrl.getEntityId() + ctrl.getIpAddress() + neighbor.getRemoteInterfaceIp() );
	                listenToHelloStarted = true;
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
