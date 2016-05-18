package no.mil.fnse.repository;

import java.net.InetAddress;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import no.mil.fnse.model.Controller;
import no.mil.fnse.model.Peer;

@Service
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
		// TODO Auto-generated method stub
		return null;
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

	public Collection<Peer> getAllPeersWithController(Controller controller) {
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
			sessionFactory.getCurrentSession().delete(peer);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}
	

}
