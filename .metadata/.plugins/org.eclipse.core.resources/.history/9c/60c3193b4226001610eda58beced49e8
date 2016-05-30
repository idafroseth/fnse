package no.mil.fnse.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: Peer.java 29 2016-05-24 19:28:08Z idamfro $
 */
@Entity
@Table(name="globalconfiguration")
public class GlobalConfiguration {
	
	@JsonIgnore
	private int id;
	
	@JsonIgnore
	private String name;
	private BgpConfig bgpConfig;
	private NtpConfig ntpConfig;
	private MsdpConfig msdpConfig;


//	private Collection<Distributelist> distributelists;
//	private Collection<Prefixlist> prefixlist;
	
	
	@JsonIgnore
	static Logger logger = Logger.getLogger(GlobalConfiguration.class);
	
	
	// -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------
    @Id
	@GeneratedValue
	@Column(name = "GLOBALCONFIG_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "NAME", unique=true, nullable=false)
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BGPCONFIG_ID")
	public BgpConfig getBgpConfig() {
		return bgpConfig;
	}


	public void setBgpConfig(BgpConfig bgpConfig) {
		this.bgpConfig = bgpConfig;
	}

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "NTPCONFIG_ID")
	public NtpConfig getNtpConfig() {
		return ntpConfig;
	}


	public void setNtpConfig(NtpConfig ntpConfig) {
		this.ntpConfig = ntpConfig;
	}

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MSDPCONFIG_ID")
	public MsdpConfig getMsdpConfig() {
		return msdpConfig;
	}


	public void setMsdpConfig(MsdpConfig msdpConfig) {
		this.msdpConfig = msdpConfig;
	}	

}
