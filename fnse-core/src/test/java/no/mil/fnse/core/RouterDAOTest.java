package no.mil.fnse.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.repository.RouterDAO;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(RootConfig.class)
//@Component
public class RouterDAOTest {


//	@Autowired
//	RouterDAO hibernateRouterDAO;
//	
//	Router router;
//	
//	@Before
//	public void init() throws UnknownHostException{
//		router = new Router();
//		router.setManagementIp(InetAddress.getByName("47.0.0.1"));
//	}
//	
//	@Test
//	public void persistRouter(){
//		int id = hibernateRouterDAO.saveRouter(router);
//		System.out.println(id);
//		assertEquals(hibernateRouterDAO.getRouter(id), router);
//	}
//	
//	@Test
//	public void getAllNational() throws UnknownHostException{
//		hibernateRouterDAO.saveRouter(router);
//		Collection<Router> national = new ArrayList<Router>();
//		Router rn1 = new Router();
//		rn1.setManagementIp(InetAddress.getByName("47.0.0.1"));
//		rn1.setNational(true);
//		Router rn2 = new Router();
//		rn2.setManagementIp(InetAddress.getByName("47.0.0.1"));
//		rn2.setNational(true);
//		national.add(rn1);
//		national.add(rn2);
//		hibernateRouterDAO.saveRouter(rn1);
//		hibernateRouterDAO.saveRouter(rn2);
		
//		ArrayList<Router> naionalDB = (ArrayList<Router>) hibernateRouterDAO.getAllNationalRouters();
//		assertTrue(naionalDB.containsAll(national));
//		assertFalse(naionalDB.contains(router));
//	}
}
