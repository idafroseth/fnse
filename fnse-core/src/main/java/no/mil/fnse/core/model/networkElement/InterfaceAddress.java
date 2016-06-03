package no.mil.fnse.core.model.networkElement;

import java.net.InetAddress;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "interfaceaddress")
public class InterfaceAddress {

	@JsonIgnore
	int id;
	InetAddress ip;
	String netmask;

	@JsonIgnore
	static Logger logger = Logger.getLogger(InterfaceAddress.class);
	
	public InterfaceAddress(){
		
	}
	public InterfaceAddress(InetAddress ip , String netmask){
		this.ip = ip;
		this.netmask = netmask;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
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
		InterfaceAddress other = (InterfaceAddress) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (netmask == null) {
			if (other.netmask != null)
				return false;
		} else if (!netmask.equals(other.netmask))
			return false;
		return true;
	}
	@Id
	@GeneratedValue
	@Column(name = "INTERFACEADDRESS_ID", unique = true, nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "IP_ADDRESS")
	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	
	@Column(name = "NETMASK")
	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	@Override
	public String toString(){
		if(ip != null){
			return ip.toString().substring(1) + " " +netmask;
		}
		return null;
	}
}
