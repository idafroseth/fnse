package no.mil.fnse.core.repository.hibernate;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Collection;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.Router;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.repository.PeerDAO;

@Transactional
@Component("hibernatePeerDAO")
public class HibernatePeerDAO implements PeerDAO{
	
	static Logger logger = Logger.getLogger(HibernatePeerDAO.class);
	
	@Autowired
	public SessionFactory sessionFactory;
	
	public HibernatePeerDAO(){
		
	}
	public HibernatePeerDAO(SessionFactory session){
		this.sessionFactory = session;
	}
	
	public int savePeer(Peer peer) {
		try{
			if(getPeerByIp(peer.getRemoteInterfaceIp(),peer.getLocalInterfaceIp())==null){
				int id = (Integer) sessionFactory.getCurrentSession().save(peer);
				logger.info("New peer added: peer(" + peer.getLocalInterfaceIp()+", " + peer.getRemoteInterfaceIp()+")");
				return id;
			}else{
				return -1;
			}
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}

	public Peer getPeer(int id) {
		try {
			return (Peer) sessionFactory.getCurrentSession().get(Peer.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Peer getPeerByIp(InetAddress local, InetAddress remote) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.eq("remoteInterfaceIp", remote));
			criteria.add(Restrictions.eq("localInterfaceIp", local));
			return (Peer) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Peer> getAllPeers() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM Peer order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Collection<Peer> getAllPeersWithLocalIp(InetAddress localIp) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.eq("localInterfaceIp", localIp));
			return criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Collection<Peer> getAllPeersWithRemoteIp(InetAddress remoteIp) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.eq("remoteInterfaceIp", remoteIp));
			return criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Collection<Peer> getAllPeersWithSDNController(SDNController controller) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.eq("controller", controller));
			return criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public void delPeer(Peer peer) {
		try {
			if (peer == null) {
				logger.error("The deleted peer did not exist");
			}
			sessionFactory.getCurrentSession().delete(peer);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}
	
	public void updatePeer(Peer peer) {
		try{
			Peer peerToChange = getPeerByIp(peer.getLocalInterfaceIp(), peer.getRemoteInterfaceIp());
			peerToChange.setDeadTime(peer.getDeadTime());
			peerToChange.setStatus(peer.getStatus());
			sessionFactory.getCurrentSession().update(peerToChange);
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);

		}
	}
	
	public Collection<Peer> getAllDeadPeers(Timestamp time) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.le("deadTime", time));
			return  criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}
}
