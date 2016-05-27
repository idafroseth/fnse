package no.mil.fnse.core.southbound;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import no.mil.fnse.core.model.BgpConfig;
import no.mil.fnse.core.model.BgpPeer;
import no.mil.fnse.core.model.NetworkInterface;
import no.mil.fnse.core.model.Router;
import no.mil.fnse.core.southbound.RouterSouthboundDAO;

@Component("vtyRouterDAO")
public class VtyRouterDAO implements RouterSouthboundDAO {

	static Logger logger = Logger.getLogger(VtyRouterDAO.class);

	public BgpConfig getBgpConfig(Router router) {
		BgpConfig bgpConfig = new BgpConfig();
		String response = router.getVty().send("show run | sec router bgp ");
		bgpConfig.setAsn(findNextWordAfter("router bgp", response));
		bgpConfig.setRouterId(findNextWordAfter("router-id", response));
		bgpConfig.setNational(true);
		if (bgpConfig.getAsn() == null || bgpConfig.getRouterId() == null) {
			return null;
		}
		return bgpConfig;
	}

	@Override
	public InetAddress getIpMrouteSource(Router router, String multicastGroup, InetAddress remotePeer) {
		String response = router.getVty().send("show ip mroute " + remotePeer.toString().substring(1) + " "
				+ multicastGroup + " | include Incoming interface");
		logger.debug("Trying to getIpMrouteSource: show ip mroute " + remotePeer.toString().substring(1) + " "
				+ multicastGroup + " | include Incoming interface");

		String ifName = findFirstWordWithPattern("Ethernet", response);

		logger.debug(ifName + " Is this the GI from " + response);

		return getInterfaceIp(router, ifName);
	}

	/**
	 * Get the primary interface ip of a named interface.
	 * 
	 * @param router
	 * @param interfaceName
	 * @return
	 */
	private InetAddress getInterfaceIp(Router router, String interfaceName) {
		try {
			return InetAddress.getByName(findFirstWordWithPattern("\\.",
					router.getVty().send("show run interface " + interfaceName + " | include ip address")));
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
	public InetAddress getSecondaryInterfaceIp(Router router, String interfaceName) {
		try {
			return InetAddress.getByName(findFirstWordWithPattern("\\.",
					router.getVty().send("show run interface " + interfaceName + " | include secondary")));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			logger.error("Attached failed: " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<BgpPeer> getBGPNeighbors(Router router) {

		return null;
	}

	@Override
	public HashSet<NetworkInterface> getNetworkInterfaces(Router router) {
		HashSet<NetworkInterface> networkIf = new HashSet<NetworkInterface>();
		System.out.println("The router VTY is connected ? " + router.getVty());
		String listInterfaces = router.getVty().send("sh interfaces  summary");
		System.out.println("This is fetched from router: " + listInterfaces);
		Collection<String> iflist = findAllWordWithPattern("GigabitEthernet", listInterfaces);
		 iflist.addAll(findAllWordWithPattern("tunnel", listInterfaces));
		 iflist.addAll(findAllWordWithPattern("loopback", listInterfaces));
		for (String interfaceName : iflist) {
			System.out.println("Found interface name: " + interfaceName);
			NetworkInterface gigabit = new NetworkInterface();
			gigabit.setInterfaceName(interfaceName);
			gigabit.setIpAddress(getInterfaceIp(router, interfaceName));
			networkIf.add(gigabit);
		}
		return networkIf;
	}

	/**
	 * Search for a searchString in a text and return the entire word. return
	 * null if the text does not contain the pattern.
	 * 
	 * @param searchString
	 * @param text
	 * @return the first word containing the pattern
	 */
	public String findFirstWordWithPattern(String searchString, String text) {

		String sPattern = "(?i)\\b\\S*" + searchString + "\\S*\\b";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	public Collection<String> findAllWordWithPattern(String searchPattern, String text) {

		String sPattern = "(?i)\\b\\S*" + searchPattern + "\\S*\\b";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(text);
		Collection<String> result = new HashSet<String>();
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result;
	}

	public String findNextWordAfter(String previousPatter, String text) {
		// String result ="";
		String sPattern = "(?<=" + previousPatter + ")\\s*(\\S+)";
		Pattern pattern = Pattern.compile(sPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;

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
	// System.out.println("found: "+s);
	// }
	// }

}
