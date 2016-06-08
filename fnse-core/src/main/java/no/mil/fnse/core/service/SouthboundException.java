package no.mil.fnse.core.service;

public class SouthboundException extends Exception {

	private static final long serialVersionUID = 1L;

	public SouthboundException() { super(); }

	public SouthboundException(String message) { super(message); }

	public SouthboundException(String message, Throwable cause) { super(message, cause); }

	public SouthboundException(Throwable cause) { super(cause); }
	
}