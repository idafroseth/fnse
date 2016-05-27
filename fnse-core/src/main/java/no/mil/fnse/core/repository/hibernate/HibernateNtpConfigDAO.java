package no.mil.fnse.core.repository.hibernate;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.repository.NtpConfigDAO;
import no.mil.fnse.core.model.NtpConfig;

@Component("hibernateNtpConfigurationDAO")
public class HibernateNtpConfigDAO implements NtpConfigDAO {
	
	static Logger logger = Logger.getLogger(HibernateNtpConfigDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public HibernateNtpConfigDAO() {

	}

	public HibernateNtpConfigDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	public int saveNtpConfig(NtpConfig ntpConfig) {
		try {
			if (getNtpConfigByAddress(ntpConfig.getNtpAddress()) == null) {
				int id = (Integer) sessionFactory.getCurrentSession().save(ntpConfig);
				logger.info("New ntpConfig added to db: " + ntpConfig.getNtpAddress());
				return id;
			} else {
				return -1;
			}
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}

	public Collection<NtpConfig> getAllNationalNtpConfigs() {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NtpConfig.class);
			criteria.add(Restrictions.eq("national", true));
			return (Collection<NtpConfig>) criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public NtpConfig getNtpConfig(int id) {
		try {
			return (NtpConfig) sessionFactory.getCurrentSession().get(NtpConfig.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public NtpConfig getNtpConfigByAddress(String address) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NtpConfig.class);
			criteria.add(Restrictions.eq("ntpAddress", address));
			return (NtpConfig) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<NtpConfig> getAllNtpConfigs() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM NtpConfig order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public void delNtpConfig(NtpConfig ntpConfig) {
		try {
			sessionFactory.getCurrentSession().delete(ntpConfig);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}


	public void updateNtpConfig(NtpConfig ntpConfig) {
		// TODO Auto-generated method stub
		
	}

}
