package no.mil.fnse.core.model.networkElement;

import java.util.Collection;

import org.apache.log4j.Logger;

public class GigabitEthernet extends NetworkInterface{
	
	private Collection<SubInterface> subinterfaces;

	
	static Logger logger = Logger.getLogger(GigabitEthernet.class);
	
	public GigabitEthernet(){
	}

	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	
	public Collection<SubInterface> getSubinterfaces() {
		return subinterfaces;
	}

	public void setSubinterfaces(Collection<SubInterface> subinterfaces) {
		this.subinterfaces = subinterfaces;
	}
	
	

}
