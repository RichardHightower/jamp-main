package org.jamp.websocket.impl;

@SuppressWarnings("serial")
public class NotSendableException extends RuntimeException {

	public NotSendableException() {
	}

	public NotSendableException( String message ) {
		super( message );
	}

	public NotSendableException( Throwable cause ) {
		super( cause );
	}

	public NotSendableException( String message , Throwable cause ) {
		super( message, cause );
	}

}