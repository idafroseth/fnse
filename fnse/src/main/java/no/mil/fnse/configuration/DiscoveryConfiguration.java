package no.mil.fnse.configuration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.mil.fnse.service.DiscoveryServiceImpl;

@Configuration
@ConfigurationProperties(ignoreUnknownFields=false, prefix = "discovery")
public class DiscoveryConfiguration {
	
	@NotNull
	public int timetolive;

	public boolean loopbackmode;

	public InetAddress group;
	
	@NotNull
	public int port;

	
	
	@Bean
	public MulticastSocket serverSocket() throws NumberFormatException, IOException{
		MulticastSocket serverSocket = new MulticastSocket(0);
		serverSocket.setTimeToLive(timetolive);
		serverSocket.setLoopbackMode(loopbackmode);
		return serverSocket;
	}
	
	@Bean
	public MulticastSocket clientSocket() throws IOException{
		MulticastSocket clientSocket = new MulticastSocket(port);
		clientSocket.joinGroup(group);
		return clientSocket;
		
	}
	
	@Bean
	public DiscoveryServiceImpl discoveryService(){
		DiscoveryServiceImpl discoveryService = new DiscoveryServiceImpl();
		discoveryService.setGroup(group);
//		discoveryService.setClientSocket(clientSocket());
//		discoveryService.setServerSocket(serverSocket());
		return discoveryService ;
	}
	
	@Bean 
	public SystemConfiguration systemConfiguration(){
		return new SystemConfiguration();
	}

	public int getTimetolive() {
		return timetolive;
	}

	public void setTimetolive(int timetolive) {
		this.timetolive = timetolive;
	}

	public boolean getLoopbackmode() {
		return loopbackmode;
	}

	public void setLoopbackmode(boolean loopbackmode) {
		this.loopbackmode = loopbackmode;
	}

	public InetAddress getGroup() {
		return group;
	}

	public void setGroup(InetAddress group) {
		this.group = group;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	
}
