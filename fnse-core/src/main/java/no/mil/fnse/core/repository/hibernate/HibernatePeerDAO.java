package no.mil.fnse.core.repository.hibernate;

import java.sql.Timestamp;
import java.util.Collection;


import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.repository.PeerDAO;

@Repository("hibernatePeerDAO")
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
			if(getPeerByIp(peer.getLocalInterfaceIp(),peer.getRemoteInterfaceIp())==null){
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

	public Peer getPeerByIp(String local, String remote) {
		try {
			String hqlController = "SELECT p from Peer p "+"where p.localInterfaceIp = :localIp " + "and p.remoteInterfaceIp = :remoteIp ";
			Query queryControllers = sessionFactory.getCurrentSession().createQuery(hqlController);
			queryControllers.setString("localIp", local);
			queryControllers.setString("remoteIp", remote);
			return (Peer) queryControllers.uniqueResult();

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

	@Override
	public Collection<Peer> getAllPeersWithLocalIp(String localIp) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.eq("localInterfaceIp", localIp));
			return criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Override
	public Collection<Peer> getAllPeersWithRemoteIp(String remoteIp) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Peer.class);
			criteria.add(Restrictions.eq("remoteInterfaceIp", remoteIp));
			return criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Override
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

	@Override
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
	
	@Override
	public void updatePeer(Peer peer) {
		try{
			sessionFactory.getCurrentSession().update(peer);
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);

		}
	}
	
	@Override
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
