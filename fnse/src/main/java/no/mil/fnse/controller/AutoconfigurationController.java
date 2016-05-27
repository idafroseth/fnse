package no.mil.fnse.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.mil.fnse.core.model.GlobalConfiguration;
import no.mil.fnse.service.AutoconfigurationService;

@RestController
public class AutoconfigurationController {
	
	@Autowired
	AutoconfigurationService defaultAutoconfService;

	static Logger logger = Logger.getLogger(AutoconfigurationController.class);
	/**
	 * To fetch the tunnel configuration.
	 * @return json
	 */
    @RequestMapping("/api/configuration/local/{remote-ip}/{local-ip}")
    public String getLocalConfig() {
        return "Here you go, I will send you the configuration!";
    }
    
    @RequestMapping("/api/configuration/global/{localIp}/{remoteIp}")
    public GlobalConfiguration getGlobalConfig(@PathVariable String remoteIp, @PathVariable String localIp) {
    	logger.info("Trying to access the global configuration of local: " + localIp);
        return defaultAutoconfService.getGlobalConfiguration(localIp);
    }
}
