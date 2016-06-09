package no.mil.fnse.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.configuration.SystemConfiguration;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.values.PeerStatus;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.SouthboundException;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;


public class DiscoveryServiceImpl implements DiscoveryService {

	static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);

	
	private InetAddress group;

	@Autowired
	private RepositoryService defaultreposervice;

	@Autowired
	private RouterSouthboundDAO vtyRouterDAO;

	@Autowired
	private MulticastSocket serverSocket;

	@Autowired
	private MulticastSocket clientSocket;

	private DatagramPacket helloPacket;

	@Autowired
	private SystemConfiguration systemConfiguration;

	public static LinkedList<Peer> configurationQueue = new LinkedList<Peer>();

	public static LinkedList<Peer> deadQueue = new LinkedList<Peer>();

	public DiscoveryServiceImpl() {

	}


	@PostConstruct
	public void getHelloMessage() throws SocketException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			byte[] helloMessage = mapper.writeValueAsBytes(systemConfiguration.getNationalController());
			helloPacket = new DatagramPacket(helloMessage, helloMessage.length, getGroup(), clientSocket.getLocalPort() );
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	boolean isListening = false;

	/**
	 * Sends a HELLO message every HELLO_INTERVAL
	 */
	@Async
	public void sendHello() {
		while(true){
			try {
				serverSocket.send(helloPacket);
				logger.debug("Sending HELLO  ");
				Thread.sleep(systemConfiguration.getNationalController().getHelloInterval()*1000);
	
			} catch (IOException | InterruptedException re) {
				logger.error("Attached failed" + re);
			}
		}

	}

	@Async
	public void listenHello() {
		logger.debug("***********STARTING TO Listen for HELLO!!!");
		byte[] buf = new byte[47];
		while (true) {
			logger.debug("LISTENING");

			// Receive the information
			DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
			try {
				clientSocket.receive(msgPacket);
				logger.info("Message is recived! " + msgPacket.getData().toString());

				String msg = new String(buf, 0, buf.length);

				ObjectMapper mapper = new ObjectMapper();

				SDNController ctrl = mapper.readValue(msg, SDNController.class);

				if (ctrl.getEntityId() != systemConfiguration.getNationalController().getEntityId()) {
					logger.debug("Found neighbor with id " + ctrl.getEntityId() + " Message was " + msg);

					helloReceived(ctrl, msgPacket.getAddress().toString().substring(1));
				}

			} catch (IOException re) {
				logger.error("Attached failed: " + re);
			}
		}

	}

	private void helloReceived(SDNController ctrl, String remoteIp)
			throws JsonParseException, JsonMappingException, IOException {

		Peer neighbor = new Peer();

		neighbor.setRemoteInterfaceIp(remoteIp);
		neighbor.setLocalInterfaceIp(findLocalIp(remoteIp));
		neighbor.setController(ctrl);
		neighbor.setDeadTime(
				new Timestamp(System.currentTimeMillis() + neighbor.getController().getHelloInterval() * 3 * 1000));

		Peer peerInDB = defaultreposervice.getPeerByIp(neighbor.getLocalInterfaceIp(), neighbor.getRemoteInterfaceIp());

		if (peerInDB == null) {
			System.out.println("PEER DOES NOT EXISTS IN DATABASE YET??");
			persistPeer(neighbor);
		} else {
			updatePeer(peerInDB, neighbor);
		}
	}

	private void persistPeer(Peer neighbor) {
		defaultreposervice.addSdnController(neighbor.getController());
		neighbor.setStatus(PeerStatus.DISCOVERED);
		neighbor.setId(defaultreposervice.addPeer(neighbor));
		if (neighbor.getId() == 0) {
			logger.error("DiscoveryServiceImpl.discoverdNeighbor Trying to add the correct router but it is null ");
			return;
		}
		configurationQueue.add(neighbor);
	}

	private void updatePeer(Peer peerInDB, Peer neighbor) {
		// Vi må sjekke om den er markert som DEAD - hvis den er det må vi
		// sette den som discovered

		if (peerInDB.getStatus().equals(PeerStatus.DEAD)) {
			neighbor.setStatus(PeerStatus.DISCOVERED);
		} else {
			logger.debug("Setting the status to: " + peerInDB.getStatus());
			neighbor.setStatus(peerInDB.getStatus());
		}
		logger.debug("Trying to update: " + peerInDB.getId());
		
		defaultreposervice.updatePeer(neighbor);
	}

	@Async
	@Override
	public void checkDeadPeer() {

		while (true) {
			logger.debug("***********STARTING TO CHECK FOR DEAD PEERS!!!");
			Collection<Peer> deadPeers = defaultreposervice.getAllDeadPeers(new Timestamp(System.currentTimeMillis()));
			logger.info("Dead peers: " + deadPeers);
			for (Peer dead : deadPeers) {
				if(dead.getStatus()== PeerStatus.DEAD){
					continue;
				}
				dead.setStatus(PeerStatus.DEAD);
				defaultreposervice.updatePeer(dead);
				logger.info("Found dead peer " + dead.getRemoteInterfaceIp());
				deadQueue.add(dead);
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private String findLocalIp(String remoteIp) {
		for (Router router : systemConfiguration.getNationalRouters()) {
			logger.debug("Trying to fetch the local ip");
			if(vtyRouterDAO.getRouter() != router || vtyRouterDAO.getRouter() == null){
				vtyRouterDAO.setRouter(router);
			}
			String localIp;
			try {
				InetAddress ip =  vtyRouterDAO.getIpMrouteSource(group.toString().substring(1), remoteIp);
				if(ip == null){
					return null;
				}
				localIp = ip.toString().substring(1);
			} catch (SouthboundException e) {
				logger.error("AttachedFailed " + e);
				e.printStackTrace();
				return null;
			}
			if (localIp == null) {
				continue;
			}
			return localIp;
		}
		logger.error("Could not find localIp...");
		return null;
	}
	
	///////////////////////
	// GETTERS AND SETTERS
	///////////////////////

	@Override
	public void setGroup(InetAddress group) {
		 this.group = group;
	}

	@Override
	public InetAddress getGroup() {
		return this.group;
	}

	@Override
	public MulticastSocket getServerSocket() {
		return serverSocket;
	}

	@Override
	public void setServerSocket(MulticastSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	@Override
	public MulticastSocket getClientSocket() {
		return clientSocket;
	}

	@Override
	public void setClientSocket(MulticastSocket clientSocket) {
		this.clientSocket = clientSocket;
	}	
	
	
	
}
