package no.mil.fnse.autoconfiguration.model.values;

import com.fasterxml.jackson.annotation.JsonInclude;

import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.MsdpConfig;
import no.mil.fnse.core.model.networkElement.NetworkInterface;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalConfiguration {
	NetworkInterface tunnel;
	BgpConfig bgp;
	MsdpConfig msdp;
	
	public NetworkInterface getTunnel() {
		return tunnel;
	}
	public void setTunnel(NetworkInterface tunnel) {
		this.tunnel = tunnel;
	}
	public BgpConfig getBgp() {
		return bgp;
	}
	public void setBgp(BgpConfig bgp) {
		this.bgp = bgp;
	}
	public MsdpConfig getMsdp() {
		return msdp;
	}
	public void setMsdp(MsdpConfig msdp) {
		this.msdp = msdp;
	}
	
	
	
}
