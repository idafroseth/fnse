package no.mil.fnse.core.repository.hibernate;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.repository.NetworkInterfaceDAO;

@Repository("hibernateNetworkInterfaceDAO")
public class HibernateNetworkInterfaceDAO implements NetworkInterfaceDAO {
	static Logger logger = Logger.getLogger(HibernateNetworkInterfaceDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public HibernateNetworkInterfaceDAO() {

	}

	public HibernateNetworkInterfaceDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	public int saveNetworkInterface(NetworkInterface networkInterface) {
		try {
			System.out.println("Trying to check if the NE is already saved");
			if (getNetworkInterfaceByName(networkInterface.getInterfaceName()) == null) {
				int id = (Integer) sessionFactory.getCurrentSession().save(networkInterface);
				logger.info("New networkInterface added to db: " + networkInterface.getInterfaceAddress());
				return id;
			} else {
				return -1;
			}
		} catch (RuntimeException re) {
			logger.error("Attached failed when saving NetworkInterface " + re);
			re.printStackTrace();
			return -1;
		}
	}
	
	public NetworkInterface getNetworkInterfaceByAddress(InterfaceAddress interfaceAdr) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NetworkInterface.class);
			criteria.add(Restrictions.eq("interfaceAddress", interfaceAdr));
			return (NetworkInterface) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			re.printStackTrace();
			return null;
		}
	}


	@Override
	public NetworkInterface getNetworkInterfaceByName(String interfaceName) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NetworkInterface.class);
			criteria.add(Restrictions.eq("interfaceName", interfaceName));
			return (NetworkInterface) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			re.printStackTrace();
			return null;
		}
	}


	public NetworkInterface getNetworkInterface(int id) {
		try {
			return (NetworkInterface) sessionFactory.getCurrentSession().get(NetworkInterface.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			re.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<NetworkInterface> getAllNetworkInterfaces() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM NetworkInterface order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			re.printStackTrace();
			return null;
		}
	}

	public void delNetworkInterface(NetworkInterface networkInterface) {
		try {
			sessionFactory.getCurrentSession().delete(networkInterface);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			re.printStackTrace();
		}
	}


	public void updateNetworkInterface(NetworkInterface networkInterface) {
		try{
			NetworkInterface ifToChange = getNetworkInterface(networkInterface.getId());
			ifToChange.setInterfaceAddress(networkInterface.getInterfaceAddress());
			ifToChange.setDescription(networkInterface.getDescription());
			ifToChange.setIpv6Address(networkInterface.getIpv6Address());
			sessionFactory.getCurrentSession().update(ifToChange);
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			re.printStackTrace();
 
		}
		
	}

}
