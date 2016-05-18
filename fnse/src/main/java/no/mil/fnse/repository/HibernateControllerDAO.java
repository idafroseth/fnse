package no.mil.fnse.repository;

import java.net.InetAddress;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import no.mil.fnse.model.Controller;

@Service
@Component("hibernateControllerDAO")
public class HibernateControllerDAO implements ControllerDAO {

	static Logger logger = Logger.getLogger(HibernateControllerDAO.class);
	
	@Autowired
	public SessionFactory sessionFactory;
	
	public HibernateControllerDAO(){
		
	}
	public HibernateControllerDAO(SessionFactory session){
		this.sessionFactory = session;
	}
	
	public int saveController(Controller controller) {
		try{
			if(getControllerByIp(controller.getIpAddress())==null){
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


	public Controller getController(int id) {
		try {
			return (Controller) sessionFactory.getCurrentSession().get(Controller.class, id);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public Controller getControllerByIp(InetAddress ip) {
		try {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Controller.class);
			criteria.add(Restrictions.eq("ipAddress", ip));
			return (Controller) criteria.uniqueResult();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Controller> getAllControllers() {
		try {
			return sessionFactory.getCurrentSession().createQuery("FROM Controller order by id").list();
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
			return null;
		}
	}

	public void delController(Controller controller) {
		try {
			sessionFactory.getCurrentSession().delete(controller);
		} catch (RuntimeException re) {
			logger.error("Attached failed" + re);
		}
		
	}

}