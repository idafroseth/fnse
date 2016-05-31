package no.mil.fnse.configuration;

import org.springframework.stereotype.Component;

@Component("configurationStatus")
public class Status {
	
	public static Boolean databaseIsConfigured = false;
	
	public static Boolean helloSocketIsReady = false;
	
	

	public Boolean isDatabaseIsConfigured() {
		return databaseIsConfigured;
	}

	public void setDatabaseIsConfigured(Boolean databaseIsConfigured) {
		Status.databaseIsConfigured = databaseIsConfigured;
	}
	
	public Boolean getDatabaseIsConfigured( ) {
		return Status.databaseIsConfigured;
	}

}
