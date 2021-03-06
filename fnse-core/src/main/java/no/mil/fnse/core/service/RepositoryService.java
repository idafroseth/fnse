package no.mil.fnse.core.service;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;

import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.model.SystemConfiguration;
import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.values.PeerStatus;

public interface RepositoryService {

	/**
	 * Persisting a peer
	 * 
	 * @param peer
	 * @return generated peerId
	 */
	int addPeer(Peer peer);

	/**
	 * Remove all dependencies to a peer and deletes is from the database
	 * 
	 * @param PeerId
	 */
	void delPeer(int PeerId);

	/**
	 * Updates the deadtime and the status of a peer.
	 * 
	 * @param PeerId
	 * @param deadTime
	 * @param status
	 */
	void updatePeer(Peer neighbor);

	
	public Collection<Peer> getAllDeadPeers(Timestamp currentTime);
	
	/**
	 * 
	 * @param localIp
	 * @param remoteIp
	 * @return
	 */
	Peer getPeerByIp(String localIp, String remoteIp);
	
	int addSystemConfiguration(SystemConfiguration config);

	int addSdnController(SDNController ctrl);

	void addPeerToSdnController(int peerId, int ctrlId);

	SDNController getSdnController(int ctrlId);

	void removePeerFromSdnController(int peerId, int sdnCtrlId);

	Collection<Router> getAllNationalRouters();

	int addRouter(Router router);
	
	int addInterfaceAddress(InterfaceAddress ip);

	/**
	 * Returns the parent router connecting the remote peer
	 * 
	 */
//	Router getRouterByRemotePeer(int peerId);

//	void addRemotePeerToRouter(int routerId, int peerId);

	void addNetworkInterfaceToRouter(int routerId, int neId);

//	void removeRemotePeerFromRouter(int routerId, int peerId);

	Router getRouterByNetworkInterface(int neId);

	NetworkInterface getNetworkInterfaceByAddress(InterfaceAddress ip);
	
	InterfaceAddress getInterfaceAddressByIp(InetAddress ip);
	
	Router getRouterByLocalIp(InetAddress ip);

	void delRouter(int routerId);

	void addGlobalConfigurationToRouter(int globalConfigId, int routerId);

	int addGlobalConfiguration(GlobalConfiguration global);

	void delGlobalConfiguration(int globalconfId);

	int addBgpConfiguration(BgpConfig bgpConfig);

	void delBgpConfiguration(int bgpConfigId);

	BgpConfig getBgpConfiguration(int bggConfigId);

	void addBgpToGlobalConfiguration(int globalConfigurationId, int bgpConfigId);

	void removeBgpfromGlobalConfiguration(int globalConfigurationId, int bgpConfigId);

	void addMsdpToGlobalConfiguration(int globalConfigurationId, int msdpConfigId);

	void removeMsdpfromGlobalConfiguration(int globalConfigurationId, int msdpConfigId);

	void addNtpToGlobalConfiguration(int globalConfigurationId, int ntpConfigId);

	void removeNtpfromGlobalConfiguration(int globalConfigurationId, int ntpConfigId);

	int addNetworkInterface(NetworkInterface ne);
	
	 void updateNetworkInterface(NetworkInterface ne);

	void delNetworkInterface(NetworkInterface neId);
	
	void delSDNController(SDNController ctrl);

	void addTunnelToNeighbor(int id, int tunnelId);

	void addBgpConfigToPeer(int peerId, int bgpId);

}
