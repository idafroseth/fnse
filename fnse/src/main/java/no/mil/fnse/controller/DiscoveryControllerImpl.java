package no.mil.fnse.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.configuration.DiscoveryConfiguration;
import no.mil.fnse.model.SDNController;
import no.mil.fnse.repository.PeerDAO;
import no.mil.fnse.model.Peer;
import no.mil.fnse.service.DiscoveryService;
import no.mil.fnse.southbound.dao.RouterDAO;
import no.mil.fnse.southbound.model.Router;

@Service
public class DiscoveryControllerImpl implements DiscoveryController {
	static Logger logger = Logger.getLogger(DiscoveryControllerImpl.class);

	@Autowired
	DiscoveryService discoveryServiceImpl;
	boolean isListening = false;
	

	@Autowired
	PeerDAO hibernatePeerDAO;
	
	@Autowired
	RouterDAO vtyRouterDAO;
	
	/**
	 * Sends a HELLO message every HELLO_INTERVAL
	 */
	@Scheduled(initialDelay = 5 * 1000, fixedRate = 30000)
	public void sendHello() {
		try {
			DiscoveryConfiguration.SERVER_SOCKET.send(DiscoveryConfiguration.HELLO_PACKET);
			logger.info("Sending HELLO");
		} catch (IOException re) {
			logger.error("Attached failed" + re);
		}
	}

	@Scheduled(initialDelay = 1 * 1000, fixedRate = 10 * 1000)
	public void listenHello() {
		logger.info("Setting up listener port");
		if (!isListening) {
			byte[] buf = new byte[85];
			while (true) {
				// Receive the information
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				try {
					DiscoveryConfiguration.CLIENT_SOCKET.receive(msgPacket);
					String msg = new String(buf, 0, buf.length);
					ObjectMapper mapper = new ObjectMapper();
					SDNController ctrl;

					ctrl = mapper.readValue(msg, SDNController.class);
					System.out.println("Found neighbor with id " + ctrl.getEntityId());
					
					//Hvis det er en melding sent fra denne controlleren så overser vi denne pakken
					if (ctrl.getEntityId() != DiscoveryConfiguration.DISCOVERY_CONFIG.getNATIONAL_CONTROLLER()
							.getEntityId()) {

						Peer neighbor = new Peer();
						
						
						neighbor.setRemoteInterfaceIp(msgPacket.getAddress());

						neighbor.setLocalInterfaceIp(findLocalIp(msgPacket.getAddress()));
						neighbor.setController(ctrl);
						neighbor.setDeadTime(new Timestamp(System.currentTimeMillis() + neighbor.getController().getHelloInterval() * 3 * 1000));
						discoveryServiceImpl.discoverdNeighbor(neighbor);
						logger.info("Discovered:" + ctrl.getEntityId() + ctrl.getIpAddress()
								+ neighbor.getRemoteInterfaceIp() + " attached to " + neighbor.getLocalInterfaceIp());
						
						// listenerThreads--;
					}
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

	@Scheduled(initialDelay = 10 * 1000, fixedDelay = 10 * 1000)
	@Override
	public void checkDeadPeer() {
		Collection<Peer> deadPeers = hibernatePeerDAO.getAllDeadPeers(new Timestamp(System.currentTimeMillis()));
		System.out.println("Dead peers: " + deadPeers);
		System.out.println("If the list isnt empty we have to remove the configuration of all the peers");
	}
	
	private InetAddress findLocalIp(InetAddress remoteIp){
		for(Router router : DiscoveryConfiguration.DISCOVERY_CONFIG.getNetworkElements()){
			return(vtyRouterDAO.getIpMrouteSource(router, remoteIp).get(0));
		}
		return null;
	}

}
