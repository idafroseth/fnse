package no.mil.fnse.core.model.networkElement;

public class BgpPeer {
	private String routerId;
	private String remoteAs;
	private Short ebgpMultihop;
	private AddressfamilySetting ipv4Settings;
	private AddressfamilySetting ipv6Settings;
	
	public BgpPeer(){
		
	}
	
	// -------------------------------------------------------------------------
    // Hashcode and Equals
    // -------------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((remoteAs == null) ? 0 : remoteAs.hashCode());
		result = prime * result + ((routerId == null) ? 0 : routerId.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BgpPeer other = (BgpPeer) obj;
		if (remoteAs == null) {
			if (other.remoteAs != null)
				return false;
		} else if (!remoteAs.equals(other.remoteAs))
			return false;
		if (routerId == null) {
			if (other.routerId != null)
				return false;
		} else if (!routerId.equals(other.routerId))
			return false;
		return true;
	}


	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	
	public String getRouterId() {
		return routerId;
	}

	public void setRouterId(String routerId) {
		this.routerId = routerId;
	}

	public String getRemoteAs() {
		return remoteAs;
	}

	public void setRemoteAs(String remoteAs) {
		this.remoteAs = remoteAs;
	}

	public Short getEbgpMultihop() {
		return ebgpMultihop;
	}

	public void setEbgpMultihop(Short ebgpMultihop) {
		this.ebgpMultihop = ebgpMultihop;
	}

	public AddressfamilySetting getIpv4Settings() {
		return ipv4Settings;
	}

	public void setIpv4Settings(AddressfamilySetting ipv4Settings) {
		this.ipv4Settings = ipv4Settings;
	}

	public AddressfamilySetting getIpv6Settings() {
		return ipv6Settings;
	}

	public void setIpv6Settings(AddressfamilySetting ipv6Settings) {
		this.ipv6Settings = ipv6Settings;
	}
	
	
	

}
