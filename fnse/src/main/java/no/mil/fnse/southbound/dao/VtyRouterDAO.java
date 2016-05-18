package no.mil.fnse.southbound.dao;

import java.net.InetAddress;
import java.util.List;

import no.mil.fnse.configuration.DiscoveryConfiguration;
import no.mil.fnse.model.Peer;
import no.mil.fnse.southbound.model.BGPPeer;

public class VtyRouterDAO implements RouterDAO extends Connections {

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
	public InetAddress getIpMrouteSource(InetAddress remotePeer) {
		
		String interfaceName = vty.write("show ip mroute "+remotePeer.toString() + " " + DiscoveryConfiguration.DISCOVERY_CONFIG.getMULTICAST_GROUP() +" | include Incoming interface");
		return NetworkElement.getNetworkInterfaceByName(interfaceName).getAddressList();;
	}

	@Override
	public List<BGPPeer> getBGPNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}

}
