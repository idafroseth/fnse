package no.mil.fnse.southbound.dao;

import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import no.mil.fnse.configuration.DiscoveryConfiguration;
import no.mil.fnse.model.Peer;
import no.mil.fnse.service.DiscoveryServiceImpl;
import no.mil.fnse.southbound.model.BGPPeer;
import no.mil.fnse.southbound.model.Router;

@Component("vtyRouterDAO")
public class VtyRouterDAO implements RouterDAO {

	static Logger logger = Logger.getLogger(DiscoveryServiceImpl.class);

	@Override
	public boolean addNeighbor(Peer neighbor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteNeighbor(Peer neighbor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Peer> getAllConfiguredPeer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<InetAddress> getIpMrouteSource(Router router, InetAddress remotePeer) {
		String response = router.getVty().write("show ip mroute " + remotePeer.toString().substring(1) + " "
					+ DiscoveryConfiguration.DISCOVERY_CONFIG.getMULTICAST_GROUP().toString().substring(1) + " | include Incoming interface");
			logger.info("Trying to getIpMrouteSource: show ip mroute " + remotePeer.toString().substring(1) + " "
					+ DiscoveryConfiguration.DISCOVERY_CONFIG.getMULTICAST_GROUP() + " | include Incoming interface");
		
		return null;
	 }

	@Override
	public List<BGPPeer> getBGPNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}

}
