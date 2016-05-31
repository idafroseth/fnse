package no.mil.fnse.discovery.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.configuration.DatabaseInitialization;
import no.mil.fnse.configuration.DiscoveryConfiguration;
import no.mil.fnse.configuration.Status;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.values.PeerStatus;
import no.mil.fnse.core.repository.PeerDAO;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.service.SouthboundException;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;

@Service
@Component("discoveryServiceImpl")
public class DiscoveryServiceImpl implements DiscoveryService {
	static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);

	@Autowired
	RepositoryService defaultreposervice;

	@Autowired
	PeerDAO hibernatePeerDAO;

	@Autowired
	RouterSouthboundDAO vtyRouterDAO;

	public static LinkedList<Peer> configurationQueue = new LinkedList<Peer>();

	public static LinkedList<Peer> deadQueue = new LinkedList<Peer>();

	public DiscoveryServiceImpl() {

	}

	boolean isListening = false;

	/**
	 * Sends a HELLO message every HELLO_INTERVAL
	 */
	@Scheduled(fixedRate = 40 * 1000)
	public void sendHello() {
		logger.debug("***********TRYING TO STARTING TO SEND HELLO!!!");
		if (Status.helloSocketIsReady) {
			logger.debug("***********STARTING TO SEND HELLO!!!");
			try {
				DiscoveryConfiguration.SERVER_SOCKET.send(DiscoveryConfiguration.HELLO_PACKET);
				logger.info("Sending HELLO");
			} catch (IOException re) {
				logger.error("Attached failed" + re);
			}
		}
	}

	@Scheduled(initialDelay = 40*1000, fixedRate = Long.MAX_VALUE)
	public void listenHello() {

		if (!isListening) {


			logger.info("***********STARTING TO Listen for HELLO!!!");
			isListening = true;
			byte[] buf = new byte[47];
			while (true) {

				// Receive the information
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				try {
					DiscoveryConfiguration.CLIENT_SOCKET.receive(msgPacket);
					logger.debug("Message is recived! " + msgPacket.getData().toString());

					String msg = new String(buf, 0, buf.length);

					ObjectMapper mapper = new ObjectMapper();

					SDNController ctrl = mapper.readValue(msg, SDNController.class);

					logger.info("Found neighbor with id " + ctrl.getEntityId() + " Message was " + msg);

					// Hvis det er en melding sent fra denne controlleren så
					// overser vi denne pakken
					if (ctrl.getEntityId() != DatabaseInitialization.CONFIGURATION.getNationalController()
							.getEntityId()) {

						Peer neighbor = new Peer();

						neighbor.setRemoteInterfaceIp(msgPacket.getAddress());
						neighbor.setLocalInterfaceIp(findLocalIp(msgPacket.getAddress()));
						neighbor.setController(ctrl);
						neighbor.setDeadTime(new Timestamp(
								System.currentTimeMillis() + neighbor.getController().getHelloInterval() * 3 * 1000));
						discoverdNeighbor(neighbor);
						logger.info("Discovered:" + ctrl.getEntityId() + ctrl.getIpAddress()
								+ neighbor.getRemoteInterfaceIp() + " attached to " + neighbor.getLocalInterfaceIp());

					}
				} catch (IOException re) {
					logger.error("Attached failed: " + re);
				}
			}
		}
	}


	@Scheduled(fixedRate = 10 * 1000)
	public void checkDeadPeer() {
		logger.debug("***********STARTING TO CHECK FOR DEAD PEERS!!!");
		Collection<Peer> deadPeers = hibernatePeerDAO.getAllDeadPeers(new Timestamp(System.currentTimeMillis()));
		logger.info("Dead peers: " + deadPeers);
		for (Peer dead : deadPeers) {
			defaultreposervice.updatePeer(dead.getId(), null, PeerStatus.DEAD, null);
			deadQueue.add(dead);
		}
	}

	/**
	 * * 1) Sjekk om nabo eksiterer i databasen 1.1) Hvis nei - 1.1.1) sjekk om
	 * kontroller ligger i databasend 1.1.1.1) Hvis nei, legg til kontroller
	 * (som også relasjon til peer) 1.1.1.2) Hvis ja, oppdater kontroller med
	 * peer (forutsetter at peer er lagt inn) 1.1.2) Legg til nabo med riktig
	 * 1.2)Hvis Ja: 1.2.1) oppdater tidstempel og kontroller
	 * 
	 * @param neighbor
	 */
	private void discoverdNeighbor(Peer neighbor) {
		Peer peerInDB = defaultreposervice.getPeerByIp(neighbor.getLocalInterfaceIp(), neighbor.getRemoteInterfaceIp());

		if (peerInDB == null) {
			defaultreposervice.addSdnController(neighbor.getController());
			neighbor.setStatus(PeerStatus.DISCOVERED);
			neighbor.setRouter(defaultreposervice.getRouterByLocalIp(neighbor.getLocalInterfaceIp()));
			System.out.println("DID WE GET THE CORRECT ROUTER?? " + neighbor.getRouter());

			if(neighbor.getRouter()==null){
				System.out.println("DiscoveryServiceImpl.discoverdNeighbor Trying to add the correct router but it is null ");
			}
			defaultreposervice.addPeer(neighbor);
			logger.info(
					"New peer discovered: " + neighbor.getLocalInterfaceIp() + ", " + neighbor.getRemoteInterfaceIp());
			configurationQueue.add(neighbor);
			System.out.println("Adding peer to queue: " + configurationQueue.isEmpty());
		} else {
			// Vi må sjekke om den er markert som DEAD - hvis den er det må vi
			// sette den som discovered

			if (peerInDB.getStatus().equals(PeerStatus.DEAD)) {
				peerInDB.setStatus(PeerStatus.DISCOVERED);
			} else {
				logger.info("Setting the status to: " + peerInDB.getStatus());
				peerInDB.setStatus(peerInDB.getStatus());
			}
			logger.info("Trying to update: " + peerInDB.getId());
			defaultreposervice.updatePeer(peerInDB.getId(), neighbor.getDeadTime(), neighbor.getStatus(), null);
		}

	}

	private InetAddress findLocalIp(InetAddress remoteIp) {
		for (Router router : DatabaseInitialization.CONFIGURATION.getNetworkElements()) {
			logger.info("Trying to fetch the local ip");
			vtyRouterDAO.setRouter(router);
			InetAddress localIp;
			try {
				localIp = vtyRouterDAO.getIpMrouteSource(
						DatabaseInitialization.CONFIGURATION.getDiscoveryMulticastGroup().toString().substring(1),
						remoteIp);
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
}
