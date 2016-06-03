package no.mil.fnse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.mil.fnse.configuration.DatabaseInitialization;
import no.mil.fnse.service.AutoconfigurationService;
import no.mil.fnse.service.DiscoveryService;

@Service
public class SystemController {

	@Autowired 
	DatabaseInitialization databaseInitialization;
	
	@Autowired
	DiscoveryService discoveryService;
	
	@Autowired
	AutoconfigurationService autoconfigurationService;
	
	public void start(){
	
		System.out.println("INITIALIZING THE DATABASE");
		databaseInitialization.configure();
		
		System.out.println("STARTING THE DISCOVERY SERVICE");
		discoveryService.sendHello();
		discoveryService.listenHello();
	
		System.out.println("STARTING THE AUTOCONF SERVICE");
		autoconfigurationService.removeDeadPeers();
		autoconfigurationService.configureNewPeers();
	
	}
}
