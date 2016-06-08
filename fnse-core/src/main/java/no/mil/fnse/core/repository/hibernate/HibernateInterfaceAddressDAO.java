package no.mil.fnse.core.repository.hibernate;

import java.net.InetAddress;
import java.util.Collection;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.repository.InterfaceAddressDAO;

@Repository("hibernateInterfaceAddressDAO")
public class HibernateInterfaceAddressDAO implements InterfaceAddressDAO{
	static Logger logger = Logger.getLogger(HibernateInterfaceAddressDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public HibernateInterfaceAddressDAO() {

	}

	public HibernateInterfaceAddressDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	
	public int saveInterfaceAddress(InterfaceAddress interfaceAddress) {
		try {
			if (getInterfaceAddressByIp(interfaceAddress.getIp()) == null) {
				int id = (Integer) sessionFactory.getCurrentSession().save(interfaceAddress);
				logger.info("New interfaceAddress added to db: " + interfaceAddress.getIp());
//				sessionFactory.getCurrentSession().flush();
				return id;
			} else {
				return -1;
			}
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}

	
	public InterfaceAddress getInterfaceAddressByIp(InetAddress ip) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InterfaceAddress.class);
			criteria.add(Restrictions.eq("ip", ip));
			return (InterfaceAddress) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}
	
	public InterfaceAddress getInterfaceAddress(int id) {
		try {
			return (InterfaceAddress) sessionFactory.getCurrentSession().get(InterfaceAddress.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<InterfaceAddress> getAllInterfaceAddresss() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM InterfaceAddress order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	
	public void delInterfaceAddress(InterfaceAddress interfaceAddress) {
		try {
			sessionFactory.getCurrentSession().delete(interfaceAddress);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}

	
	public void updateInterfaceAddress(InterfaceAddress interfaceAddress) {
		try{
			InterfaceAddress ifAdrToChange = getInterfaceAddressByIp(interfaceAddress.getIp());
			ifAdrToChange.setNetmask(interfaceAddress.getNetmask());
			sessionFactory.getCurrentSession().update(ifAdrToChange);
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);

		}
		
	}


}

