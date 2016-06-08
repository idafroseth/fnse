package no.mil.fnse.core.repository.hibernate;

import java.util.Collection;


import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.repository.SDNControllerDAO;

@Repository("hibernateSDNControllerDAO")
public class HibernateSDNControllerDAO implements SDNControllerDAO{
	static Logger logger = Logger.getLogger(HibernateSDNControllerDAO.class);
	
	@Autowired
	public SessionFactory sessionFactory;
	
	public HibernateSDNControllerDAO(){
		
	}
	public HibernateSDNControllerDAO(SessionFactory session){
		this.sessionFactory = session;
	}
	
	public int saveSDNController(SDNController controller) {
		try{
			if(getSDNControllerByIp(controller.getIpAddress())==null){
				int id = (Integer) sessionFactory.getCurrentSession().save(controller);
				logger.info("New controller added: " + controller.getIpAddress());
				return id;
			}else{
				return -1;
			}
		}catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return -1;
		}
	}


	public SDNController getSDNController(int id) {
		try {
			return (SDNController) sessionFactory.getCurrentSession().get(SDNController.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public SDNController getSDNControllerByIp(String ip) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SDNController.class);
			criteria.add(Restrictions.eq("ipAddress", ip));
			return (SDNController) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Collection<SDNController> getAllSDNControllers() {
		try {
			return  sessionFactory.getCurrentSession().createQuery("FROM SDNController order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public void delSDNController(SDNController controller) {
		try {
			sessionFactory.getCurrentSession().delete(controller);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
		
	}
}
