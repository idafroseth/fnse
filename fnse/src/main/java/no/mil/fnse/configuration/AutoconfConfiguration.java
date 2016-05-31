package no.mil.fnse.configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.mil.fnse.autoconfiguration.service.AutoconfigurationService;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;

@Component("autoconfConfiguration")
public class AutoconfConfiguration {

	public static LinkedList<NetworkInterface> grePool = new LinkedList<NetworkInterface>();

	boolean isStarted = false;
	
	@Autowired
	AutoconfigurationService defaultAutoconfService;


	@Scheduled(initialDelay=25*1000, fixedDelay = Long.MAX_VALUE)
	public void configureGreAddressPool() {
		if(isStarted){
			return;
		}
		isStarted = true;
		try {
			for (int lastOctett = 1; lastOctett < 255; lastOctett = lastOctett + 4) {
				NetworkInterface ne = new NetworkInterface();
				ne.setInterfaceName("tunnel" + lastOctett + "00");
				ne.setDescription("IOP tunnel unused");
				ne.setInterfaceAddress(new InterfaceAddress(
						InetAddress.getByName(DatabaseInitialization.CONFIGURATION.getNationalController().getEntityId()
								+ ".1.0." + lastOctett),
						"255.255.255.252"));

				grePool.add(ne);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
