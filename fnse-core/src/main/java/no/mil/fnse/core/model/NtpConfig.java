package no.mil.fnse.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 
 * @author Ida Marie Frøseth
 * @version $Id: NtpConfig.java 29 2016-05-25 19:28:08Z idamfro $
 */
@Entity
@Table(name="ntpconfig")
public class NtpConfig {
	@JsonIgnore
	private int id;
	
	private String ntpAddress;
	
	private String stratum;
	
	@JsonIgnore
	private boolean national;

	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column( nullable=false)
	public String getNtpAddress() {
		return ntpAddress;
	}

	public void setNtpAddress(String ntpAddress) {
		this.ntpAddress = ntpAddress;
	}

	@Column
	public String getStratum() {
		return stratum;
	}

	public void setStratum(String stratum) {
		this.stratum = stratum;
	}

	@Column(name="NTP_NATIONAL_FLAG")
	public boolean isNational() {
		return national;
	}

	public void setNational(boolean national) {
		this.national = national;
	}
	
	
	
}
