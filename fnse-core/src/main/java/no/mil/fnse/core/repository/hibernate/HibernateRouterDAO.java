package no.mil.fnse.core.repository.hibernate;

import java.net.InetAddress;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.repository.RouterDAO;

@Repository("hibernateRouterDAO")
public class HibernateRouterDAO implements RouterDAO {
	static Logger logger = Logger.getLogger(HibernateRouterDAO.class);
	
	@Autowired
	public SessionFactory sessionFactory;
	
	public HibernateRouterDAO(){
		
	}
	public HibernateRouterDAO(SessionFactory session){
		this.sessionFactory = session;
	}
	
	public int saveRouter(Router controller) {
		try{
			if(getRouterByManagementIp(controller.getManagementIp())==null){
				int id = (Integer) sessionFactory.getCurrentSession().save(controller);
				logger.info("New router added to db: " + controller.getManagementIp());
				return id;
			}else{
				return -1;
			}
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}

	
	public Collection<Router> getAllNationalRouters() {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Router.class);
			criteria.add(Restrictions.eq("national", true));
			return (Collection<Router>) criteria.list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Router getRouter(int id) {
		try {
			return (Router) sessionFactory.getCurrentSession().get(Router.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Router getRouterByManagementIp(InetAddress ip) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Router.class);
			criteria.add(Restrictions.eq("managementIp", ip));
			return (Router) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Router> getAllRouters() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM Router order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}
	
	public void delRouter(int router) {
		try {
			sessionFactory.getCurrentSession().delete(router);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
		
	}


	
	public void updateRouter(Router router) {
		try{
			Router routerToChange = getRouterByManagementIp(router.getManagementIp());
			routerToChange.setUsername(router.getUsername());
			routerToChange.setPassword(router.getPassword());
			routerToChange.setNetworkInterfaces(router.getNetworkInterfaces());
			routerToChange.setGlobalConfiguration(router.getGlobalConfiguration());
			sessionFactory.getCurrentSession().update(routerToChange);
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);

		}
		
	}

}
