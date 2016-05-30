package no.mil.fnse.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import no.mil.fnse.autoconfiguration.model.values.ConfigType;
import no.mil.fnse.autoconfiguration.service.AutoconfigurationService;
import no.mil.fnse.core.model.DnsConfig;
import no.mil.fnse.core.model.NtpConfig;
import no.mil.fnse.core.model.Peer;
import no.mil.fnse.core.model.SipConfig;
import no.mil.fnse.core.model.SystemWideConfiguration;
import no.mil.fnse.core.model.networkElement.TunnelInterface;

@RestController
public class AutoconfigurationController {
	
	@Autowired
	AutoconfigurationService defaultAutoconfService;

	static Logger logger = Logger.getLogger(AutoconfigurationController.class);

    
    /**
     * Returns the local configuration for a peer. Including the GRE tunnel interface, MSDP and BGP peering information. 
     * @param remoteIp
     * @param localIp
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/configuration/local/{localIp}/{remoteIp}")
    public Peer getLocalConfiguration(@PathVariable String remoteIp, @PathVariable String localIp) {
    	logger.info("Trying to access the global configuration of local: " + localIp);
        return defaultAutoconfService.getLocalConfiguration(localIp, remoteIp);
    }
    
    /**
     * Returns the local configuration for a peer. Including the GRE tunnel interface, MSDP and BGP peering information. 
     * @param remoteIp
     * @param localIp
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/configuration/local/{localIp}/{remoteIp}/gre")
    public TunnelInterface getGreConfiguration(@PathVariable String remoteIp, @PathVariable String localIp) {
    	logger.info("Trying to access the global configuration of local: " + localIp);
        return defaultAutoconfService.getTunnelInterface(localIp, remoteIp);
    }
    
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, value = "/api/configuration/local/{localIp}/{remoteIp}/gre")
    public TunnelInterface setGreConfiguration(@PathVariable String remoteIp, @PathVariable String localIp, @RequestBody TunnelInterface config ) {
    	logger.debug("Trying to configure the GRE tunnel interface address " + localIp);
        return defaultAutoconfService.setTunnelInterface(localIp, remoteIp, config);
    }
    
    /**
     * Returns the system wide configuration. Including the DNS, SIP, NTP,  
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/configuration/global")
    public SystemWideConfiguration getGlobalConfiguration() {
    	logger.info("Trying to access the global configuration");
        return defaultAutoconfService.getSystemWideConfiguration();
    }
    
    /**
     * Returns a special configuration   
     * @return 
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/configuration/global/{configType}")
    public <E> Object getGlobalConfiguration(@PathVariable ConfigType configType) {
    	switch(configType){
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
     * @return 
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/api/configuration/global/{configType}")
    public <E> Object getGlobalConfiguration(@PathVariable ConfigType configType, @RequestBody E configuration) {
    	switch(configType){
    		case SIP:
    			return defaultAutoconfService.updateSipConfig((SipConfig) configuration);
    		case DNS:
    			return defaultAutoconfService.updateDnsConfig((DnsConfig) configuration);
    		case NTP:
    			return defaultAutoconfService.updateNtpConfig( (NtpConfig) configuration);
    		default:
    			return null;
    	}
    }
    
}
