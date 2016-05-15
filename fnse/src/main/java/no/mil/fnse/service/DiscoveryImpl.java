package no.mil.fnse.service;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.mil.fnse.model.Controller;
import no.mil.fnse.model.Peer;
import no.mil.fnse.model.values.PeerStatus;
import no.mil.fnse.repository.HibernateControllerDAO;
import no.mil.fnse.repository.HibernatePeerDAO;

@Service
public class DiscoveryImpl implements Discovery{

	@Autowired
	private HibernateControllerDAO hibernateControllerDAO;
	
	@Autowired
	private HibernatePeerDAO hibernatePeerDAO;
	
	public Peer getPeer(InetAddress localIp, InetAddress remoteIp) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Peer> getAllPeers() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Peer> getDeadPeers(Date currentTime) {
		
		return null;
	}

	public void setPeerStatus(String peerId, PeerStatus status) {
		// TODO Auto-generated method stub
		
	}

	public void addPeerToController(String peerId, Controller ctrl) {
		// TODO Auto-generated method stub
		
	}

	public int addPeer(Peer peer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
