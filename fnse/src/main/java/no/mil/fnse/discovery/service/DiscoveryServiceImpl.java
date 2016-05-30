package no.mil.fnse.discovery.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.configuration.DatabaseInitialization;
import no.mil.fnse.configuration.DiscoveryConfiguration;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.values.PeerStatus;
import no.mil.fnse.core.repository.PeerDAO;
import no.mil.fnse.core.service.RepositoryService;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;

@Service
@ConditionalOnProperty(name = "configurationStatus.databaseIsConfigured", havingValue = "true", matchIfMissing = false) 
@Component("discoveryServiceImpl")
public class DiscoveryServiceImpl implements DiscoveryService {
	static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);

	@Autowired
	RepositoryService defaultreposervice;

	@Autowired
	PeerDAO hibernatePeerDAO;

	@Autowired
	RouterSouthboundDAO vtyRouterDAO;

	public DiscoveryServiceImpl() {

	}

	boolean isListening = false;

	/**
	 * Sends a HELLO message every HELLO_INTERVAL
	 */
	@Scheduled(fixedRate = 30*1000)
	public void sendHello() {
		System.out.println("***********STARTING TO SEND HELLO!!!");
		if (DiscoveryConfiguration.discoveryIsConfigured) {
			try {
				DiscoveryConfiguration.SERVER_SOCKET.send(DiscoveryConfiguration.HELLO_PACKET);
				logger.info("Sending HELLO");
			} catch (IOException re) {
				logger.error("Attached failed" + re);
			}
		}
	}

	@Scheduled(initialDelay = 15 * 1000, fixedDelay = 360000)
	public void listenHello() {
		System.out.println("***********STARTING TO Listen for HELLO!!!");
		logger.info("Setting up listener port");
		if (!isListening) {
			isListening = true;
			byte[] buf = new byte[34];
			while (true) {

				// Receive the information
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				try {
					DiscoveryConfiguration.CLIENT_SOCKET.receive(msgPacket);
					System.out.println("Message is recived! " + msgPacket.getData().toString());

					String msg = new String(buf, 0, buf.length);

					ObjectMapper mapper = new ObjectMapper();

					SDNController ctrl = mapper.readValue(msg, SDNController.class);

					logger.info("Found neighbor with id " + ctrl.getEntityId());

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

						// listenerThreads--;
					}
				} catch (IOException re) {
					logger.error("Attached failed: " + re);
				}
			}

		}

	}

	@Scheduled(initialDelay = 30 * 1000, fixedRate = 10 * 1000)
	public void checkDeadPeer() {
		System.out.println("***********STARTING TO CHECK FOR DEAD PEERS!!!");
		Collection<Peer> deadPeers = hibernatePeerDAO.getAllDeadPeers(new Timestamp(System.currentTimeMillis()));
		logger.info("Dead peers: " + deadPeers);
		for (Peer dead : deadPeers) {
			defaultreposervice.updatePeer(dead.getId(), null, PeerStatus.DEAD);
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
			defaultreposervice.addPeer(neighbor);
			logger.info(
					"New peer discovered: " + neighbor.getLocalInterfaceIp() + ", " + neighbor.getRemoteInterfaceIp());
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
			defaultreposervice.updatePeer(peerInDB.getId(), neighbor.getDeadTime(), neighbor.getStatus());
		}

	}

	private InetAddress findLocalIp(InetAddress remoteIp) {
		for (Router router : DatabaseInitialization.CONFIGURATION.getNetworkElements()) {
			logger.info("Trying to fetch the local ip");
			InetAddress localIp = vtyRouterDAO.getIpMrouteSource(router,
					DatabaseInitialization.CONFIGURATION.getDiscoveryMulticastGroup().toString().substring(1),
					remoteIp);
			if (localIp == null) {
				continue;
			}
			return localIp;
		}
		logger.error("Could not find localIp...");
		return null;
	}
}
