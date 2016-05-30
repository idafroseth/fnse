package no.mil.fnse.core.model.networkElement;

import org.apache.log4j.Logger;

public class SubInterface extends NetworkInterface {
	private Short vlan;
	private String encapsulation;
	
	static Logger logger = Logger.getLogger(SubInterface.class);
	
	public SubInterface(){
		
	}
	
	public Short getVlan() {
		return vlan;
	}
	public void setVlan(Short vlan) {
		this.vlan = vlan;
	}
	public String getEncapsulation() {
		return encapsulation;
	}
	public void setEncapsulation(String encapsulation) {
		this.encapsulation = encapsulation;
	}
	
	
	

}
