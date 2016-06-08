package no.mil.fnse.core.repository.hibernate;

import java.util.Collection;


import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.SystemConfiguration;
import no.mil.fnse.core.repository.SystemConfigurationDAO;

@Repository("hibernateSystemConfigurationDAO")
public class hibernateSystemConfigurationDAO implements SystemConfigurationDAO {

	static Logger logger = Logger.getLogger(hibernateSystemConfigurationDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public hibernateSystemConfigurationDAO() {

	}

	public hibernateSystemConfigurationDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	public int saveSystemConfiguration(SystemConfiguration config) {
		try {
			int id = (Integer) sessionFactory.getCurrentSession().save(config);
			return id;
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}

	
	public SystemConfiguration getSystemConfiguration(int id) {
		try {
			return (SystemConfiguration) sessionFactory.getCurrentSession().get(SystemConfiguration.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<SystemConfiguration> getAllSystemConfigurations() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM SystemConfiguration order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public void delSystemConfiguration(SystemConfiguration config) {
		try {
			sessionFactory.getCurrentSession().delete(config);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}

	}
}