package no.mil.fnse.repository;

import java.net.InetAddress;
import java.util.Collection;
//import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import no.mil.fnse.model.Controller;
import no.mil.fnse.model.Peer;

@Service
@Component("hibernatePeerDAO")
public class HibernatePeerDAO implements PeerDAO{
	
//	static Logger logger = Logger.getLogger(HibernatePeerDAO.class);
	
	@Autowired
	public SessionFactory sessionFactory;
	
	public HibernatePeerDAO(){
		
	}
	public HibernatePeerDAO(SessionFactory session){
		this.sessionFactory = session;
	}
	
	public int savePeer(Peer peer) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Peer getPeer(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Peer getPeerByIp(InetAddress local, InetAddress remote) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Peer> getAllPeers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Peer> getAllPeersWithLocalIp(InetAddress localIp) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Peer> getAllPeersWithRemoteIp(InetAddress remoteIp) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<Peer> getAllPeersWithController(Controller controller) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delPeer(Peer peer) {
		// TODO Auto-generated method stub
		
	}
	

}
