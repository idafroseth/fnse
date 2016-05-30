package no.mil.fnse.core.model;

public class SystemWideConfiguration {

	SipConfig sip;
	DnsConfig dns;	
	NtpConfig ntp;

	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
	
	public SipConfig getSip() {
		return sip;
	}
	public void setSip(SipConfig sip) {
		this.sip = sip;
	}
	public DnsConfig getDns() {
		return dns;
	}
	public void setDns(DnsConfig dns) {
		this.dns = dns;
	}
	public NtpConfig getNtp() {
		return ntp;
	}
	public void setNtp(NtpConfig ntp) {
		this.ntp = ntp;
	}
}
