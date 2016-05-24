package no.mil.fnse.southbound.model;

import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Router {

	private InetAddress managementIp;
	
	private String username;

	private String password;

	@JsonIgnore
	private TelnetSession vty = new TelnetSession();

	@JsonIgnore
	static Logger logger = Logger.getLogger(Router.class);

	public Router() {
	}

	public boolean openVty() {
		if (managementIp != null && username != null && password != null) {
			vty.openLine(managementIp, username, password);
			return true;
		} else {
			logger.error("username, password or management IP isnt set");
			return false;
		}
	}

	public InetAddress getManagementIp() {
		return managementIp;
	}

	public void setManagementIp(InetAddress managementIp) {
		this.managementIp = managementIp;
	}

	public TelnetSession getVty() {
		return vty;
	}

	public void setVty(TelnetSession vty) {
		this.vty = vty;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

}
