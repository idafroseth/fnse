package no.mil.fnse.core.repository.hibernate;

import java.util.Collection;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.repository.BgpConfigDAO;

@Component("hibernateBgpConfigDAO")
public class HibernateBgpConfigDAO implements BgpConfigDAO {
	static Logger logger = Logger.getLogger(HibernateBgpConfigDAO.class);

	@Autowired
	public SessionFactory sessionFactory;

	public HibernateBgpConfigDAO() {

	}

	public HibernateBgpConfigDAO(SessionFactory session) {
		this.sessionFactory = session;
	}

	@Transactional
	public int saveBgpConfig(BgpConfig bgpConfig) {
		try {
			if (getBgpConfigByRouterIdAndASN(bgpConfig.getRouterId(), bgpConfig.getAsn()) == null) {
				int id = (Integer) sessionFactory.getCurrentSession().save(bgpConfig);
				logger.info("New bgpConfig added to db: " + bgpConfig.getRouterId());
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
	public Collection<BgpConfig> getAllNationalBgpConfigs() {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BgpConfig.class);
			criteria.add(Restrictions.eq("national", true));
			return (Collection<BgpConfig>) criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Transactional
	public BgpConfig getBgpConfig(int id) {
		try {
			return (BgpConfig) sessionFactory.getCurrentSession().get(BgpConfig.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@Transactional
	public BgpConfig getBgpConfigByRouterIdAndASN(String routerId, String asn) {

		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BgpConfig.class);
			criteria.add(Restrictions.eq("routerId", routerId));
			criteria.add(Restrictions.eq("asn", asn));
			return (BgpConfig) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<BgpConfig> getAllBgpConfigs() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM BgpConfig order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public void delBgpConfig(BgpConfig bgpConfig) {
		try {
			sessionFactory.getCurrentSession().delete(bgpConfig);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
	}
}
