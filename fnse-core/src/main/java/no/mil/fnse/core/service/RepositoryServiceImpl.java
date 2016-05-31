package no.mil.fnse.core.service;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.model.SystemConfiguration;
import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.model.values.PeerStatus;
import no.mil.fnse.core.repository.BgpConfigDAO;
import no.mil.fnse.core.repository.GlobalConfigurationDAO;
import no.mil.fnse.core.repository.InterfaceAddressDAO;
import no.mil.fnse.core.repository.NetworkInterfaceDAO;
import no.mil.fnse.core.repository.PeerDAO;
import no.mil.fnse.core.repository.RouterDAO;
import no.mil.fnse.core.repository.SDNControllerDAO;
import no.mil.fnse.core.repository.SystemConfigurationDAO;
import no.mil.fnse.core.repository.hibernate.HibernateInterfaceAddressDAO;

@Transactional
@Component("defaultreposervice")
public class RepositoryServiceImpl implements RepositoryService{

	@Autowired
	PeerDAO hibernatePeerDAO;
	
	@Autowired
	SDNControllerDAO hibernateSDNControllerDAO;
	
	@Autowired
	RouterDAO hibernateRouterDAO;
	
	@Autowired
	GlobalConfigurationDAO hibernateGlobalConfigurationDAO;
	
	@Autowired
	BgpConfigDAO hibernateBgpConfigDAO;
	
	@Autowired
	NetworkInterfaceDAO hibernateNetworkInterface;
	
	@Autowired
	SystemConfigurationDAO hibernateSystemConfigurationDAO;
	
	@Autowired
	InterfaceAddressDAO hibernateInterfaceAddressDAO;
	
	
	
	static Logger logger = Logger.getLogger(RepositoryServiceImpl.class);

	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public int addPeer(Peer peer) {
		if(peer.getLocalInterfaceIp() == null || peer.getRemoteInterfaceIp()==null){
			logger.error("Trying to save peer put the local or remote ip is not set...");
			return 0;
		}
		if (hibernatePeerDAO.getPeerByIp(peer.getLocalInterfaceIp(), peer.getRemoteInterfaceIp()) != null) {
			updatePeer(hibernatePeerDAO.getPeerByIp(peer.getLocalInterfaceIp(), peer.getRemoteInterfaceIp()).getId(), peer.getDeadTime(), peer.getStatus(), peer.getTunnelInterface());
			return hibernatePeerDAO.getPeerByIp(peer.getLocalInterfaceIp(), peer.getRemoteInterfaceIp()).getId();
		}
		return hibernatePeerDAO.savePeer(peer);
	}

	@Transactional 
	public void updatePeer(int peerId, Timestamp deadTime, PeerStatus status, NetworkInterface tunnel ){
		Peer peerToChange  = hibernatePeerDAO.getPeer(peerId);
		if(deadTime != null){	
			peerToChange.setDeadTime(deadTime);
		}
		if(status != null){
			peerToChange.setStatus(status);
		}
		if(tunnel != null){
			peerToChange.setTunnelInterface(tunnel);
		}
	}
	
	@Transactional
	public Peer getPeerByIp(InetAddress localIp, InetAddress remoteIp){
		if(localIp == null || remoteIp == null){
			return null;
		}
		
		return hibernatePeerDAO.getPeerByIp(localIp, remoteIp);
	}

	
	@Override
	@Transactional public void delPeer(int peerId) {
		
		///WE must remember to remove all links from SDNcontroller first!
		String hqlController = "SELECT s from SDNController s " + "join s.peers c " + "where c.id = :peerId";
		Query queryControllers = sessionFactory.getCurrentSession().createQuery(hqlController);
		queryControllers.setInteger("peerId", peerId);
		List<SDNController> controllersOwningPeer = queryControllers.list();
		for (SDNController ctrl : controllersOwningPeer) {
			removePeerFromSdnController(peerId, ctrl.getId());
		}
//		Router r = getRouterByRemotePeer(peerId);
//		
//		if(r != null){
//			removeRemotePeerFromRouter(r.getId(),peerId);
//		}
		hibernatePeerDAO.delPeer(hibernatePeerDAO.getPeer(peerId));
	}

	@Override
	@Transactional
	public int addSdnController(SDNController ctrl) {
		return hibernateSDNControllerDAO.saveSDNController(ctrl);
	}
	
	@Transactional
	public SDNController getSdnController(int ctrlId){
		return hibernateSDNControllerDAO.getSDNController(ctrlId);
	}

	@Override
	@Transactional
	public void addPeerToSdnController(int peerId, int ctrlId) {
		// TODO Auto-generated method stub
		SDNController sdnCtrl = getSdnController(ctrlId);
		if(sdnCtrl == null){
			return;
		}
		Collection<Peer> sdnCtrlPeers = sdnCtrl.getPeers();
		if (sdnCtrlPeers.isEmpty()) {
			sdnCtrlPeers = new HashSet<Peer>();
		}
		sdnCtrlPeers.add(hibernatePeerDAO.getPeer(peerId));
		sdnCtrl.setPeers(sdnCtrlPeers);
		sessionFactory.getCurrentSession().update(sdnCtrl);
		
	}
	
	@Override
	@Transactional
	public void removePeerFromSdnController(int peerId, int ctrlId){
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Peer.class);
		SDNController sdnCtrl = getSdnController(ctrlId);
		if(sdnCtrl == null){
			return;
		}
		Collection<Peer> sdnCtrlPeers = sdnCtrl.getPeers();
		if (sdnCtrlPeers.isEmpty()) {
			sdnCtrlPeers = new HashSet<Peer>();
		}
		sdnCtrlPeers.remove(hibernatePeerDAO.getPeer(peerId));
		sdnCtrl.setPeers(sdnCtrlPeers);
		session.update(sdnCtrl);

	}

	@Override
	@Transactional
	public int addRouter(Router router) {
		// TODO Auto-generated method stub
		return hibernateRouterDAO.saveRouter(router);
	}
	
	@Transactional
	public Collection<Router> getAllNationalRouters(){
		return hibernateRouterDAO.getAllNationalRouters();
	}

	public int addSystemConfiguration(SystemConfiguration config){
		return hibernateSystemConfigurationDAO.saveSystemConfiguration(config);
	}
//	@Transactional
//	@Override
//	public Router getRouterByRemotePeer(int peerId){
//		
//		///WE must remember to remove all links from SDNcontroller first!
//		String hqlController = "SELECT s from router s " + "join s.remotePeers c " + "where c.id = :peerId";
//		Query queryControllers = sessionFactory.getCurrentSession().createQuery(hqlController);
//		queryControllers.setInteger("peerId", peerId);
//		return (Router)queryControllers.uniqueResult();
//	}
	
//	@Override
//	@Transactional
//	public void addRemotePeerToRouter(int routerId, int peerId){
//		Router router = hibernateRouterDAO.getRouter(routerId);
//		Collection<Peer> peers = router.getRemotePeers();
//		if(peers == null){
//			peers = new HashSet<Peer>();
//		}
//		peers.add(hibernatePeerDAO.getPeer(peerId));
//		router.setRemotePeers(peers);
//		sessionFactory.getCurrentSession().update(router);
//	}
//	
//	@Override
//	@Transactional
//	public void removeRemotePeerFromRouter(int routerId, int peerId){
//		Router router = hibernateRouterDAO.getRouter(routerId);
//		if(router == null){
//			logger.error("The router does not exist in database");
//			return;
//			
//		}
//		Collection<Peer> peers = router.getRemotePeers();
//		if(peers == null){
//			return;
//		}
//		peers.remove(hibernatePeerDAO.getPeer(peerId));
//		router.setRemotePeers(peers);
//		sessionFactory.getCurrentSession().update(router);
//		
//	}
	
	@Override
	@Transactional 
	public void delRouter(int routerId) {
		delGlobalConfiguration(hibernateRouterDAO.getRouter(routerId).getGlobalConfiguration().getId());
		for(NetworkInterface neIf : hibernateRouterDAO.getRouter(routerId).getNetworkInterfaces()){
			delNetworkInterface(neIf.getId());
		}
		hibernateRouterDAO.delRouter(routerId);
	}

	@Override
	@Transactional
	public Router getRouterByNetworkInterface(int neId){
		logger.debug("Trying to find routers having NE");
		String hql = "SELECT r from Router r " + "join r.networkInterfaces d " + "where d.id = :neId";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setInteger("neId", neId);
		return (Router) query.uniqueResult();
	}
	
	@Override
	@Transactional
	public NetworkInterface getNetworkInterfaceByAddress(InterfaceAddress ip){
		return hibernateNetworkInterface.getNetworkInterfaceByAddress(ip);
	}
	
	public InterfaceAddress	getInterfaceAddressByIp(InetAddress ip){
		return hibernateInterfaceAddressDAO.getInterfaceAddressByIp(ip);
	}
	
	public Router getRouterByLocalIp(InetAddress ip){
		
		NetworkInterface ne = getNetworkInterfaceByAddress(getInterfaceAddressByIp(ip));
		if(ne == null){
			return null;
		}else return getRouterByNetworkInterface(ne.getId());
		
	}
	
	@Override
	@Transactional 
	public void addGlobalConfigurationToRouter(int globalConfigId, int routerId) {
		logger.debug("DEBUG****Trying to add globalconf to router");
		Router routerToUpdate = hibernateRouterDAO.getRouter(routerId);
		if (routerToUpdate == null || hibernateGlobalConfigurationDAO.getGlobalConfiguration(globalConfigId) == null) {
			logger.error("The router or the globalconfig did not exist");
			return;
		}
		if(routerToUpdate.getGlobalConfiguration()!=null){
			logger.error("The router have alredy attahed a global config to it");
			return;
		}
		routerToUpdate.setGlobalConfiguration(hibernateGlobalConfigurationDAO.getGlobalConfiguration(globalConfigId));
		sessionFactory.getCurrentSession().update(routerToUpdate);
	}
	
	@Override
	public void addNetworkInterfaceToRouter(int routerId, int neId) {
		Router routerToUpdate = hibernateRouterDAO.getRouter(routerId);
		if (routerToUpdate == null || hibernateNetworkInterface.getNetworkInterface(neId) == null) {
			logger.error("The router or the globalconfig did not exist");
			return;
		}
		Collection<NetworkInterface> routersInterfaces =  routerToUpdate.getNetworkInterfaces();
		if(routersInterfaces == null){
			routersInterfaces = new HashSet<NetworkInterface>();
		}
		routersInterfaces.add(hibernateNetworkInterface.getNetworkInterface(neId));
		sessionFactory.getCurrentSession().update(routerToUpdate);
	}

	@Override
	@Transactional
	public int addGlobalConfiguration(GlobalConfiguration global) {
		return hibernateGlobalConfigurationDAO.saveGlobalConfiguration(global);
	}

	@Override
	@Transactional 
	public void delGlobalConfiguration(int globalconfId) {
		System.out.println("Trying to find routers responsible for globalconfig");
		String hql = "SELECT s from Router s " + "join s.globalConfiguration d " + "where d.id = :globalconfId";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setInteger("globalconfId", globalconfId);
		List<Router> routerHavingConfig = query.list();
		for (Router router : routerHavingConfig) {
//			removeGlobalConfigFromRouter(router.getId(), globalconfId);
		}
		hibernateGlobalConfigurationDAO.delGlobalConfiguration(hibernateGlobalConfigurationDAO.getGlobalConfiguration(globalconfId));
	}

	@Override
	public int addBgpConfiguration(BgpConfig bgpConfig) {
		// TODO Auto-generated method stub
		return hibernateBgpConfigDAO.saveBgpConfig(bgpConfig);
	}

	@Override
	@Transactional public void delBgpConfiguration(int bgpConfigId) {
		// TODO Auto-generated method stub
		//First remove all link from global config to this one before we delete it!!
	}

	@Override
	public BgpConfig getBgpConfiguration(int bgpConfigId) {
		return hibernateBgpConfigDAO.getBgpConfig(bgpConfigId);
	}

	@Override
	@Transactional 
	public void addBgpToGlobalConfiguration(int globalConfigurationId, int bgpConfigId) {
		Session session = sessionFactory.getCurrentSession();
		GlobalConfiguration globalConfig = hibernateGlobalConfigurationDAO.getGlobalConfiguration(globalConfigurationId);
		if (globalConfig == null) {
			return;
		}
		globalConfig.setBgpConfig(getBgpConfiguration(bgpConfigId));
		session.update(globalConfig);
		
	}

	@Override
	@Transactional 
	public void removeBgpfromGlobalConfiguration(int globalConfigurationId, int bgpConfigId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void addMsdpToGlobalConfiguration(int globalConfigurationId, int msdpConfigId) {
//		Session session = sessionFactory.getCurrentSession();
//		GlobalConfiguration globalConfig = hibernateGlobalConfigurationDAO.getGlobalConfiguration(globalConfigurationId);
//		if (globalConfig == null) {
//			return;
//		}
//		globalConfig.setMsdpConfig(hibernateMsdpConfigDAO.getMsdpConfig(msdpConfigId));
//		session.update(globalConfig);
//		
	}

	@Override
	@Transactional 
	public void removeMsdpfromGlobalConfiguration(int globalConfigurationId, int msdpConfigId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional 
	public void addNtpToGlobalConfiguration(int globalConfigurationId, int ntpConfigId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional 
	public void removeNtpfromGlobalConfiguration(int globalConfigurationId, int ntpConfigId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int addNetworkInterface(NetworkInterface ne) {
		InterfaceAddress ia = hibernateInterfaceAddressDAO.getInterfaceAddressByIp(ne.getInterfaceAddress().getIp());
		if(ia == null){
			ne.getInterfaceAddress().setId(hibernateInterfaceAddressDAO.saveInterfaceAddress(ne.getInterfaceAddress()));
		}
		return hibernateNetworkInterface.saveNetworkInterface(ne);
	}

	@Override
	@Transactional
	public void delNetworkInterface(int neId) {
		// TODO Auto-generated method stub
		
	}



}
