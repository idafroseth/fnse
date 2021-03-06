package no.mil.fnse.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;

import javax.persistence.Entity;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("vty")
public class TelnetCommunication implements ExternalCommunication {
	private TelnetClient telnetSocket = new TelnetClient();
	
	String prompt = "#";
	
	final int TELNETPORT = 23;
	
    private InputStream in = null; 
    private PrintStream out = null; 
	
	static Logger logger = Logger.getLogger(TelnetCommunication.class);


	public boolean connect(InetAddress managementIp,  String username, String password) {
		if(telnetSocket.isConnected()){
			return true;
		}
		try {
			telnetSocket.connect(managementIp, TELNETPORT);
			telnetSocket.setKeepAlive(true);
			in = telnetSocket.getInputStream();
			out = new PrintStream(telnetSocket.getOutputStream());
			login(username, password);
			return true;
		} catch (IOException e) {
			logger.error("Attached failed :" +  e);
			e.printStackTrace();
			return false;
		}
	}
	
	private void login(String username, String password) {
		try {
			
			readUntil("Username:");
			
			write(username);
			
			readUntil("Password");
			
			write(password);
			
			readUntil(prompt);
			
		} catch (IOException e) {
			logger.error("Attached failed: " + e);
			e.printStackTrace();
		}

	}
	
	/**
     * Sends the command string to the network element and return the response from it
     * @Return result from remote networking element 
     */ 
    public String send(String command) { 
        try { 
            write(command); 
            readUntil(command);//Read past the command
            String result = readUntil(prompt);
            return result; 
        } catch (IOException e) { 
            logger.error(e); 
            return null;
        } 
    } 
	
    /**
     * Closes the connection. 
     */ 
    public void close(){ 
        try { 
            telnetSocket.disconnect(); 
        } catch (IOException ioe) { 
            logger.error(ioe);
        } 
    } 
    
    public boolean isOpen(){
    	return telnetSocket.isConnected();
    }
	
    /**
     * Reads input stream until the given pattern is reached. The  
     * pattern is discarded and what was read up until the pattern is 
     * returned. 
     */ 
    private String readUntil(String pattern) throws IOException { 
        char lastChar = pattern.charAt(pattern.length() - 1); 
        char moreChar = "-".charAt(0);
        StringBuilder sb = new StringBuilder(); 
        int c; 
 
        while((c = in.read()) != -1) { 
            char ch = (char) c; 
            sb.append(ch); 
            if(ch == lastChar || ch == moreChar) { 
                String str = sb.toString(); 
                if(str.endsWith(pattern)) { 
                	logger.debug(str.substring(0, str.length() -  pattern.length()));
                    return str.substring(0, str.length() -  pattern.length()); 
                } 
                if(str.endsWith("--More--")){
                	logger.info("found more promt!!");
                	write(" ");
                }
            } 
        } 
        return null; 
    } 
    
    /**
     * Writes the value to the output stream. Does not care about the result..
     */ 
    private void write(String value) { 
        out.println(value); 
        out.flush(); 
        logger.debug(value); 
    }
    


}
