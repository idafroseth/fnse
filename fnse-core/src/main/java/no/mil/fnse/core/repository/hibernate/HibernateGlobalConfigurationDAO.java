package no.mil.fnse.core.repository.hibernate;

import java.util.Collection;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.repository.GlobalConfigurationDAO;

@Repository("hibernateGlobalConfigurationDAO")
public class HibernateGlobalConfigurationDAO implements GlobalConfigurationDAO {
	static Logger logger = Logger.getLogger(HibernateGlobalConfigurationDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public HibernateGlobalConfigurationDAO() {

	}

	public HibernateGlobalConfigurationDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	@Transactional
	public int saveGlobalConfiguration(GlobalConfiguration globalConfiguration) {
		try {
			if (getGlobalConfigurationByName(globalConfiguration.getName()) == null) {
				int id = (Integer) sessionFactory.getCurrentSession().save(globalConfiguration);
				logger.info("New globalConfiguration added to db: " + globalConfiguration.getName());
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
	public GlobalConfiguration getGlobalConfigurationByName(String name) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GlobalConfiguration.class);
			criteria.add(Restrictions.eq("name", name));
			return (GlobalConfiguration) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<GlobalConfiguration> getAllNationalGlobalConfigurations() {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(GlobalConfiguration.class);
			criteria.add(Restrictions.eq("national", true));
			return (Collection<GlobalConfiguration>) criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Transactional
	public GlobalConfiguration getGlobalConfiguration(int id) {
		try {
			return (GlobalConfiguration) sessionFactory.getCurrentSession().get(GlobalConfiguration.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Collection<GlobalConfiguration> getAllGlobalConfigurations() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM GlobalConfiguration order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Transactional
	public void delGlobalConfiguration(GlobalConfiguration globalConfiguration) {
		try {
			sessionFactory.getCurrentSession().delete(globalConfiguration);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}

	@Transactional
	public void updateGlobalConfiguration(GlobalConfiguration globalConfiguration) {
		// TODO Auto-generated method stub
		
	}
}
