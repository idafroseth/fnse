package no.mil.fnse.core.southbound;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.MsdpConfig;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;
import no.mil.fnse.core.service.SouthboundException;
import no.mil.fnse.core.service.TelnetCommunication;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;

@Component("vtyRouterDAO")
public class VtyRouterDAO implements RouterSouthboundDAO {

	static Logger logger = Logger.getLogger(VtyRouterDAO.class);

	Router router;

	@Autowired
	StringMatcher stringMatcher;

	public Router getRouter() {
		return router;
	}

	public void setRouter(Router router) {
		this.router = router;
	}

	public boolean connect() throws NullPointerException {
		if (this.router == null) {
			throw new NullPointerException();
		}
		if (this.router.getVty() == null) {
			router.setVty(new TelnetCommunication());
		}

		return router.getVty().connect(router.getManagementIp(), router.getUsername(), router.getPassword());

	}

	@Override
	public BgpConfig getBgpConfig() throws SouthboundException{
		if (!connect()) {
			throw new SouthboundException();
		}
		BgpConfig bgpConfig = new BgpConfig();
		String response = router.getVty().send("show run | sec router bgp ");
		bgpConfig.setAsn(stringMatcher.findNextWordAfter("router bgp", response));
		bgpConfig.setRouterId(stringMatcher.findNextWordAfter("router-id", response));
		bgpConfig.setNational(true);
		if (bgpConfig.getAsn() == null || bgpConfig.getRouterId() == null) {
			return null;
		}
		return bgpConfig;
	}

	@Override
	public InetAddress getIpMrouteSource(String multicastGroup, InetAddress remotePeer)  throws SouthboundException{
		if (!connect()) {
			throw new SouthboundException();
		}
		logger.info("show ip mroute " + remotePeer.toString().substring(1) + " "
				+ multicastGroup + " | include Incoming interface");
		String response = router.getVty().send("show ip mroute " + remotePeer.toString().substring(1) + " "
				+ multicastGroup + " | include Incoming interface");
		logger.debug("Trying to getIpMrouteSource: show ip mroute " + remotePeer.toString().substring(1) + " "
				+ multicastGroup + " | include Incoming interface");

		String ifName = stringMatcher.findFirstWordWithPattern("Ethernet", response);

		logger.debug(ifName + " Is this the GI from " + response);

		return getInterfaceIp(ifName).getIp();
	}

	/**
	 * Get the primary interface ip of a named interface.
	 * 
	 * @param router
	 * @param interfaceName
	 * @return
	 */
	private InterfaceAddress getInterfaceIp(String interfaceName)  throws SouthboundException{
		if (!connect()) {
			throw new SouthboundException();
		}
		try {
			InetAddress ad = InetAddress.getByName(stringMatcher.findFirstWordWithPattern("\\.",
					router.getVty().send("show run interface " + interfaceName + " | include ip address")));
			
			logger.info("ADDRESS OF INTERFACE: "+ interfaceName + " adr: " +ad);
			return new InterfaceAddress(ad,"255.255.255.0");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			logger.error("Attached failed: " + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Fetch the secondary IP address
	 * 
	 * @param router
	 * @param interfaceName
	 * @return
	 */
	public InetAddress getSecondaryInterfaceIp(String interfaceName) throws SouthboundException{
		if (!connect()) {
			throw new SouthboundException();
		}
		try {
			return InetAddress.getByName(stringMatcher.findFirstWordWithPattern("\\.",
					router.getVty().send("show run interface " + interfaceName + " | include secondary")));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			logger.error("Attached failed: " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BgpConfig> getBGPNeighbors() {

		return null;
	}

	@Override
	public HashSet<NetworkInterface> getNetworkInterfaces() throws SouthboundException{
		if (!connect()) {
			throw new SouthboundException();
		}
			HashSet<NetworkInterface> networkIf = new HashSet<NetworkInterface>();
			logger.info("The router VTY is connected ? " + router.getVty());
			String listInterfaces = router.getVty().send("sh interfaces  summary");
			logger.info("This is fetched from router: " + listInterfaces);
			Collection<String> iflist = stringMatcher.findAllWordWithPattern("GigabitEthernet", listInterfaces);
			iflist.addAll(stringMatcher.findAllWordWithPattern("tunnel", listInterfaces));
			iflist.addAll(stringMatcher.findAllWordWithPattern("loopback", listInterfaces));
			for (String interfaceName : iflist) {
				logger.info("Found interface name: " + interfaceName);
				NetworkInterface gigabit = new NetworkInterface();
				gigabit.setInterfaceName(interfaceName);
				gigabit.setInterfaceAddress(getInterfaceIp(interfaceName));
				networkIf.add(gigabit);
			}
			return networkIf;

	}

	public void configureStaticRoute(InetAddress ipNetwork, String netmask, String nextHop)  throws SouthboundException{
		if (!connect()) {
			throw new SouthboundException();
		}
		logger.info("Trying to write to the terminal" );
		router.getVty().send("configure terminal");
		logger.info(router.getVty().send("ip route " + ipNetwork.toString().substring(1) + " " + netmask + " " + nextHop ));
		router.getVty().send("end");
		logger.info("NOW the router should have a new route");
		
	}
	
	public void configureBgpPeer(String localAs, BgpConfig bgp){
		router.getVty().send("configure terminal");
		router.getVty().send("router bgp "+ localAs );
		router.getVty().send("neighbor " +bgp.getRouterId() + " remote-as "+bgp.getAsn());
		router.getVty().send("neighbor " +bgp.getRouterId() + " " + bgp.getEbgpHop());
		router.getVty().send("address-family ipv4");
		router.getVty().send("neighbor " +bgp.getRouterId() + " activate");
		router.getVty().send("address-family ipv6");
		router.getVty().send("neighbor " +bgp.getRouterId() + " activate");
		router.getVty().send("end");	
	}
	
	
	public void removeBgpPeer(String localAs, BgpConfig bgp){
		router.getVty().send("configure terminal");
		router.getVty().send("router bgp "+ localAs );
		router.getVty().send("no neighbor " +bgp.getRouterId() + " remote-as "+bgp.getAsn());
		router.getVty().send("end");	
	}
	
	
	public void configureNtpPeer(NtpConfig ntp){
		router.getVty().send("configure terminal");
		router.getVty().send("ntp peer " + ntp.getNtpAddress() + " version 4");
		router.getVty().send("end");
	}
	
	public void removeNtpPeer(NtpConfig ntp){
		router.getVty().send("configure terminal");
		router.getVty().send("no ntp peer " + ntp.getNtpAddress() + " version 4");
		router.getVty().send("end");
	}
	
	public void configureTunnel(Peer peer){
		router.getVty().send("configure terminal");
		router.getVty().send("interface tunnel"+peer.getTunnelInterface().getInterfaceName());
		if(peer.getTunnelInterface().getInterfaceAddress()!=null){
			router.getVty().send("ip address " + peer.getTunnelInterface().getInterfaceAddress().toString());
		}
		router.getVty().send("ipv6 enable");
		router.getVty().send("tunnel source "+peer.getLocalInterfaceIp());
		router.getVty().send("tunnel destination "+peer.getRemoteInterfaceIp());
		router.getVty().send("end");
	}
	
	public void removeTunnel(Peer peer){
		router.getVty().send("configure terminal");
		router.getVty().send("no interface tunnel "+peer.getTunnelInterface().getInterfaceName());
		router.getVty().send("end");
	}
	
	public void configureMsdpPeer(MsdpConfig config, String loopbackinterface){
		router.getVty().send("configure terminal");
		router.getVty().send("ip msdp peer " + config.getPeerAddress() + " connect-source " + loopbackinterface);
		router.getVty().send("end");
	}
	
	public void removeMsdpPeer(MsdpConfig config){
		router.getVty().send("configure terminal");
		router.getVty().send("no ip msdp peer " + config.getPeerAddress());
		router.getVty().send("end");
	}


	// public static void main(String[] args) {
	// VtyRouterDAO vty = new VtyRouterDAO(new Router());
	// for (String s : vty.findAllWordWithPattern("GigabitEthernet",
	// " *May 27 08:17:02.356: %SYS-5-CONFIG_I: Configured from console by
	// console"
	// + "*May 27 08:17:02.608: %LINEPROTO-5-UPDOWN: Line protocol on Interface
	// Loopback200, changed state to sh interfaces summary *: interface is up"
	// + "Interface IHQ IQD OHQ OQD RXBS RXPS TXBS TXPS TRTL"
	// +
	// "-----------------------------------------------------------------------------------------------------------------"
	// + " GigabitEthernet0/0 0 0 0 0 0 0 0 0 0"
	// + "* GigabitEthernet0/1 0 0 0 0 0 0 0 0 0"
	// + "* GigabitEthernet0/2 0 0 0 0 0 0 0 0 0"
	// + "* Loopback200 0 0 0 0 0 0 0 0 0"
	// + "* NVI0 0 0 0 0 0 0 0 0 0")) {
	// logger.info("found: "+s);
	// }
	// }

}
