package no.mil.fnse.service;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import no.mil.fnse.model.Peer;
import no.mil.fnse.model.values.PeerStatus;
import no.mil.fnse.repository.PeerDAO;
import no.mil.fnse.repository.SDNControllerDAO;

@Service
@Component("discoveryServiceImpl")
public class DiscoveryServiceImpl implements DiscoveryService {
	static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);

	@Autowired
	SDNControllerDAO hibernateSDNControllerDAO;

	@Autowired
	PeerDAO hibernatePeerDAO;
	
	public DiscoveryServiceImpl(){
		
	}

	public void checkDeadPeer() {
		// TODO Auto-generated method stub
		/*
		 * Check database where current date time > timestamp and not marked as
		 * DEAD
		 * 
		 */

	}

	@Override
	public void discoverdNeighbor(Peer neighbor) {
		// TODO Auto-generated method stub
		// Vi må legge til Peer i controller

		/*
		 * 1) Sjekk om nabo eksiterer i databasen 1.1) Hvis nei - 1.1.1) sjekk
		 * om kontroller ligger i databasend 1.1.1.1) Hvis nei, legg til
		 * kontroller (som også relasjon til peer) 1.1.1.2) Hvis ja, oppdater
		 * kontroller med peer (forutsetter at peer er lagt inn) 1.1.2) Legg til
		 * nabo med riktig 1.2)Hvis Ja: 1.2.1) oppdater tidstempel og kontroller
		 */
		Peer peerInDB = hibernatePeerDAO.getPeerByIp(neighbor.getLocalInterfaceIp(), neighbor.getRemoteInterfaceIp());
		if (peerInDB == null) {
			neighbor.setStatus(PeerStatus.DISCOVERED);
			addNeighbor(neighbor);
			logger.info("New peer discovered: " + neighbor.getLocalInterfaceIp() + ", " + neighbor.getRemoteInterfaceIp());
		} else {
			//Vi må sjekke om den er markert som DEAD - hvis den er det må vi sette den som discovered
			if(peerInDB.getStatus().equals(PeerStatus.DEAD)){
				neighbor.setStatus(PeerStatus.DISCOVERED);
			}else{
				neighbor.setStatus(peerInDB.getStatus());
			}
			hibernatePeerDAO.updatePeer(neighbor);
		}

	}

	/**
	 * Add a remote neighbor by first checking if its controller exsist. If the
	 * controller doesn´t exsist it will first add the controller before it adds
	 * the peer.
	 */
	public void addNeighbor(Peer neighbor) {
		if (hibernateSDNControllerDAO.getSDNControllerByIp(neighbor.getSDNController().getIpAddress()) == null) {
			hibernateSDNControllerDAO.saveSDNController(neighbor.getSDNController());
		}

		neighbor.setSDNController(hibernateSDNControllerDAO.getSDNControllerByIp(neighbor.getSDNController().getIpAddress()));
		hibernatePeerDAO.savePeer(neighbor);
	}

}
