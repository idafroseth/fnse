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

import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.repository.InterfaceAddressDAO;

@Component("hibernateInterfaceAddressDAO")
public class HibernateInterfaceAddressDAO implements InterfaceAddressDAO{
	static Logger logger = Logger.getLogger(HibernateInterfaceAddressDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public HibernateInterfaceAddressDAO() {

	}

	public HibernateInterfaceAddressDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	@Transactional
	public int saveInterfaceAddress(InterfaceAddress interfaceAddress) {
		try {
			if (getInterfaceAddressByIp(interfaceAddress.getIp()) == null) {
				int id = (Integer) sessionFactory.getCurrentSession().save(interfaceAddress);
				logger.info("New interfaceAddress added to db: " + interfaceAddress.getIp());
				return id;
			} else {
				return -1;
			}
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}

	@Transactional
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
	

	@Transactional
	public InterfaceAddress getInterfaceAddress(int id) {
		try {
			return (InterfaceAddress) sessionFactory.getCurrentSession().get(InterfaceAddress.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<InterfaceAddress> getAllInterfaceAddresss() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM InterfaceAddress order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Transactional
	public void delInterfaceAddress(InterfaceAddress interfaceAddress) {
		try {
			sessionFactory.getCurrentSession().delete(interfaceAddress);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}

	@Transactional
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

