package no.mil.fnse.southbound.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

public class TelnetSession {
	
	private Socket telnetSocket;
	
	boolean isconnected = false;
	
	final int TELNETPORT = 23;
	
	static Logger logger = Logger.getLogger(Router.class);


	public boolean openLine(InetAddress managementIp,  String username, String password) {
		try {
			telnetSocket = new Socket(managementIp, TELNETPORT);
			telnetSocket.setKeepAlive(true);
			login(username, password);
			return true;
		} catch (IOException e) {
			logger.error("Attached failed :" +  e);
			return false;
		}
	}

	public String write(String command){
		String response ="";
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(telnetSocket.getOutputStream())));
	
			/* read response */
			BufferedReader in = new BufferedReader(new InputStreamReader(telnetSocket.getInputStream()));
	
			String inputLine;
	
			out.println(command);
			out.flush();
			
			while ((inputLine = in.readLine()) != null) {
				response += inputLine;
			}
			return response;
		} catch (IOException e) {
			logger.error("Attached failed :" +  e);
			return null;
		}
			
		
	}
	
	private void login(String username, String password) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(telnetSocket.getOutputStream())));
	
			/* read response */
			BufferedReader in = new BufferedReader(new InputStreamReader(telnetSocket.getInputStream()));
	
			String inputLine;
	
			out.println("cisco");
			out.flush();
	
			out.println("cisco");
			out.flush();
		
			while ((inputLine = in.readLine()) != null) {
				System.out.println("reading: " + inputLine);
			}
		
			System.out.println("Login succesfull");
	
			in.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
