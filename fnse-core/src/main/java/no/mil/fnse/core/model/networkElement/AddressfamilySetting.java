package no.mil.fnse.core.model.networkElement;

public class AddressfamilySetting {
	private boolean activated;
	private String distributelistIn;
	private String distributelistOut;
	private String prefixlistIn;
	private String prefixlistOut;
	
	public AddressfamilySetting(){
		
	}
	
	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	
	public boolean isActivated() {
		return activated;
	}
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	public String getDistributelistIn() {
		return distributelistIn;
	}
	public void setDistributelistIn(String distributelistIn) {
		this.distributelistIn = distributelistIn;
	}
	public String getDistributelistOut() {
		return distributelistOut;
	}
	public void setDistributelistOut(String distributelistOut) {
		this.distributelistOut = distributelistOut;
	}
	public String getPrefixlistIn() {
		return prefixlistIn;
	}
	public void setPrefixlistIn(String prefixlistIn) {
		this.prefixlistIn = prefixlistIn;
	}
	public String getPrefixlistOut() {
		return prefixlistOut;
	}
	public void setPrefixlistOut(String prefixlistOut) {
		this.prefixlistOut = prefixlistOut;
	}
	
	

}
