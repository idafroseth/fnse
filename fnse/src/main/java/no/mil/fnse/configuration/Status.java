package no.mil.fnse.configuration;

import org.springframework.stereotype.Component;

@Component("configurationStatus")
public class Status {
	
	private boolean databaseIsConfigured;

	public boolean isDatabaseIsConfigured() {
		return databaseIsConfigured;
	}

	public void setDatabaseIsConfigured(boolean databaseIsConfigured) {
		this.databaseIsConfigured = databaseIsConfigured;
	}
	
	

}
