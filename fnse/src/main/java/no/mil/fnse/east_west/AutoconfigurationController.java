package no.mil.fnse.east_west;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import no.mil.fnse.autoconfiguration.model.values.ConfigType;
import no.mil.fnse.autoconfiguration.model.values.LocalConfiguration;
import no.mil.fnse.autoconfiguration.model.values.SystemWideConfiguration;
import no.mil.fnse.configuration.SystemConfiguration;
import no.mil.fnse.core.model.DnsConfig;
import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SipConfig;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.TunnelInterface;
import no.mil.fnse.service.AutoconfigurationService;

@RestController
public class AutoconfigurationController {

	@Autowired
	AutoconfigurationService defaultAutoconfService;

	@Autowired
	SystemConfiguration systemConfiguration;

	
	static Logger logger = Logger.getLogger(AutoconfigurationController.class);


	/**
	 * Returns the local configuration for a peer. Including the GRE tunnel
	 * interface, MSDP and BGP peering information.
	 * 
	 * @param remoteIp
	 * @param localIp
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/configuration/local/{localIp}/{remoteIp}")
	public LocalConfiguration getLocalConfiguration( @PathVariable String localIp,@PathVariable String remoteIp) {
		logger.info("Trying to access the configuration of local: " + localIp + ", " + remoteIp);
		LocalConfiguration localConfig = defaultAutoconfService.getLocalConfiguration(localIp, remoteIp);
		if(localConfig == null){
			return new LocalConfiguration();
		}
		return localConfig;
	}

	/**
	 * Returns the local configuration for a peer. Including the GRE tunnel
	 * interface, MSDP and BGP peering information.
	 * 
	 * @param remoteIp
	 * @param localIp
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/configuration/local/{localIp}/{remoteIp}/gre")
	public TunnelInterface getGreConfiguration(@PathVariable String remoteIp, @PathVariable String localIp) {
		logger.info("Trying to  access the GRE tunnel interface of: " + localIp);
		return defaultAutoconfService.getTunnelInterface(localIp, remoteIp);
	}

	@RequestMapping(method = { RequestMethod.PUT,
			RequestMethod.POST }, value = "/api/configuration/local/{localIp}/{remoteIp}/gre")
	public TunnelInterface setGreConfiguration(@PathVariable String remoteIp, @PathVariable String localIp,
			@RequestBody TunnelInterface config) {
		logger.debug("Trying to POST access the GRE tunnel interface of " + localIp);
		return defaultAutoconfService.setTunnelInterface(localIp, remoteIp, config);
	}

	/**
	 * Returns the system wide configuration. Including the DNS, SIP, NTP,
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/configuration/global")
	public SystemWideConfiguration getGlobalConfiguration(HttpServletRequest request) {
		logger.info("Trying to access the global configuration");
		logger.debug(request.getRemoteAddr());
		return defaultAutoconfService.getSystemWideConfiguration();
	}

	/**
	 * Returns a special configuration
	 * 
	 * @return
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/configuration/global/{configType}")
	public <E> Object getGlobalConfiguration(@PathVariable ConfigType configType, HttpServletRequest request) {
		switch (configType) {
		case SIP:
			return defaultAutoconfService.getSipConfig();
		case DNS:
			return defaultAutoconfService.getDnsConfig();
		case NTP:
			return defaultAutoconfService.getNtpConfig();
		default:
			return null;
		}
	}

	/**
	 * Returns the system wide configuration. Including the DNS, SIP, NTP,
	 * 
	 * @return
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/api/configuration/global/{configType}")
	public <E> Object getGlobalConfiguration(@PathVariable ConfigType configType, @RequestBody E configuration) {
		switch (configType) {
		case SIP:
			return defaultAutoconfService.updateSipConfig((SipConfig) configuration);
		case DNS:
			return defaultAutoconfService.updateDnsConfig((DnsConfig) configuration);
		case NTP:
			return defaultAutoconfService.updateNtpConfig((NtpConfig) configuration);
		default:
			return null;
		}
	}

}
