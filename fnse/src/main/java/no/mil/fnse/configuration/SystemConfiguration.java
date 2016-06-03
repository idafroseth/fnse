package no.mil.fnse.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.mil.fnse.core.model.SDNController;
import no.mil.fnse.core.model.networkElement.Router;

@Component("systemConfiguration")
public class SystemConfiguration {
	
	
	private SDNController nationalController;
	
	@JsonProperty("network_elements")
	private ArrayList<Router> nationalRouters = new ArrayList<Router>(5);
	
	static Logger logger = Logger.getLogger(SystemConfiguration.class);

	
	public SDNController getNationalController() {
		return nationalController;
	}
	
	public void setNationalController(SDNController nationalController) {
		this.nationalController = nationalController;
	}
	
	public ArrayList<Router> getNationalRouters() {
		return nationalRouters;
	}

	public void setNationalRouters(ArrayList<Router> nationalRouters) {
		this.nationalRouters = nationalRouters;
	}
	
	@PostConstruct
	public void init(){
		System.out.println("POST CONSTRUCTING");
		ObjectMapper mapper = new ObjectMapper();
		try {
			SystemConfiguration conf = mapper.readValue(new File(System.getProperty("user.dir") + "/config.json"),
					SystemConfiguration.class);

			this.nationalRouters = conf.getNationalRouters();
			this.nationalController = conf.getNationalController();
			
		
		} catch (IOException e) {
			logger.error("Attached failed: " + e);
		}
		
	}
}
	
	
	
