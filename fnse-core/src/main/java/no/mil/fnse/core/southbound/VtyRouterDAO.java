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

	public synchronized void setRouter(Router router) {
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

	private void isConnected() throws SouthboundException {
		if(router == null){
			throw new SouthboundException();
		}else if(router.getVty() == null){
			router.setVty(new TelnetCommunication());
		}
		if (router.getVty().isOpen()) {
			if (!connect()) {
				throw new SouthboundException();
			}
		}
	}

	@Override
	public synchronized BgpConfig getBgpConfig() throws SouthboundException {
		isConnected();
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
	public synchronized InetAddress getIpMrouteSource(String multicastGroup, String remotePeer) throws SouthboundException {
		isConnected();

		logger.debug("show ip mroute " + remotePeer + " " + multicastGroup + " | include Incoming interface");
		String response = router.getVty()
				.send("show ip mroute " + remotePeer + " " + multicastGroup + " | include Incoming interface");

		String ifName = stringMatcher.findFirstWordWithPattern("Ethernet", response);
		InterfaceAddress adress = getInterfaceIp(ifName);
		if(adress == null){
			return null;
		}
		return adress.getIp();
	}

	/**
	 * Get the primary interface ip of a named interface.
	 * 
	 * @param router
	 * @param interfaceName
	 * @return
	 */
	private synchronized InterfaceAddress getInterfaceIp(String interfaceName) throws SouthboundException {
		isConnected();
		try {
			String result = router.getVty().send("show run interface " + interfaceName + " | include ip address");
			String ipAddress = stringMatcher.findFirstWordWithPattern("\\.", result);
			String netmask = stringMatcher.findNextWordAfter(ipAddress, result);
			if (ipAddress == null) {
				return null;
			}
			if (ipAddress.contains("127")) {
				return null;
			}
			InetAddress ad = InetAddress.getByName(ipAddress);

			logger.debug("ADDRESS OF INTERFACE: " + interfaceName + " adr: " + ad + " with netmask " + netmask);
			return new InterfaceAddress(ad, netmask);
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
	public synchronized InetAddress getSecondaryInterfaceIp(String interfaceName) throws SouthboundException {
		isConnected();
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
	public synchronized List<BgpConfig> getBGPNeighbors() {

		return null;
	}

	@Override
	public synchronized HashSet<NetworkInterface> getNetworkInterfaces() throws SouthboundException {
		isConnected();
		HashSet<NetworkInterface> networkIf = new HashSet<NetworkInterface>();
		logger.debug("The router VTY is connected ? " + router.getVty());
		String listInterfaces = router.getVty().send("sh interfaces  summary");
		logger.debug("This is fetched from router: " + listInterfaces);
		Collection<String> iflist = stringMatcher.findAllWordWithPattern("GigabitEthernet", listInterfaces);
		iflist.addAll(stringMatcher.findAllWordWithPattern("tunnel", listInterfaces));
		iflist.addAll(stringMatcher.findAllWordWithPattern("loopback", listInterfaces));
		for (String interfaceName : iflist) {
			logger.debug("Found interface name: " + interfaceName);
			NetworkInterface gigabit = new NetworkInterface();
			gigabit.setInterfaceName(interfaceName);
			gigabit.setInterfaceAddress(getInterfaceIp(interfaceName));
			networkIf.add(gigabit);

		}
		return networkIf;

	}

	public synchronized void configureStaticRoute(InetAddress ipNetwork, String netmask, String nextHop) throws SouthboundException {
		isConnected();
		router.getVty().send("configure terminal");

		logger.debug(
				router.getVty().send("ip route " + ipNetwork.toString().substring(1) + " " + netmask + " " + nextHop));
		router.getVty().send("end");
		logger.debug("NOW the router should have a new route");

	}

	public synchronized void configureBgpPeer(String localAs, String updateSource, BgpConfig bgp) throws SouthboundException {
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("router bgp " + localAs);
		router.getVty().send("neighbor " + bgp.getRouterId() + " remote-as " + bgp.getAsn());
		router.getVty().send("neighbor " + bgp.getRouterId() + " ebgp-multihop " + bgp.getEbgpHop() + 2);
		router.getVty().send("neighbor " + bgp.getRouterId() + " update-source " + updateSource);
		router.getVty().send("address-family ipv4");
		router.getVty().send("neighbor " + bgp.getRouterId() + " activate");
		router.getVty().send("address-family ipv6");
		router.getVty().send("neighbor " + bgp.getRouterId() + " activate");
		router.getVty().send("end");
	}

	public synchronized void removeBgpPeer(String localAs, BgpConfig bgp) throws SouthboundException {
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("router bgp " + localAs);
		router.getVty().send("no neighbor " + bgp.getRouterId() + " remote-as " + bgp.getAsn());
		router.getVty().send("end");
	}

	public synchronized void configureNtpPeer(NtpConfig ntp) throws SouthboundException {
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("ntp peer " + ntp.getNtpAddress() + " version 4");
		router.getVty().send("end");
	}

	public synchronized void removeNtpPeer(NtpConfig ntp) throws SouthboundException{
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("no ntp peer " + ntp.getNtpAddress() + " version 4");
		router.getVty().send("end");
	}

	public synchronized void configureTunnel(Peer peer) throws SouthboundException{
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("interface " + peer.getTunnelInterface().getInterfaceName());
		if (peer.getTunnelInterface().getInterfaceAddress() != null) {
			router.getVty().send("ip address " + peer.getTunnelInterface().getInterfaceAddress().toString());
		}
		router.getVty().send("ipv6 enable");
		router.getVty().send("tunnel source " + peer.getLocalInterfaceIp());
		router.getVty().send("tunnel destination " + peer.getRemoteInterfaceIp());
		router.getVty().send("end");
	}

	public synchronized void removeTunnel(String interfaceName) throws SouthboundException{
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("no interface " + interfaceName);
		router.getVty().send("end");
	}

	public synchronized void configureMsdpPeer(MsdpConfig config, String loopbackinterface) throws SouthboundException{
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("ip msdp peer " + config.getPeerAddress() + " connect-source " + loopbackinterface);
		router.getVty().send("end");
	}

	public synchronized void removeMsdpPeer(MsdpConfig config) throws SouthboundException{
		isConnected();
		router.getVty().send("configure terminal");
		router.getVty().send("no ip msdp peer " + config.getPeerAddress());
		router.getVty().send("end");
	}

	@Override
	public synchronized void removeStaticRoute(InetAddress ipNetwork, String netmask, String nextHop) throws SouthboundException {
		isConnected();
		router.getVty().send("configure terminal");
		logger.debug(router.getVty()
				.send("no ip route " + ipNetwork.toString().substring(1) + " " + netmask + " " + nextHop));
		router.getVty().send("end");
		logger.debug("The route should be removed to " + ipNetwork);

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
	// logger.debug("found: "+s);
	// }
	// }

}
