package me.thelunarfrog.frogannounce.exceptions;

public class InvalidWorldException extends Exception {
	/**
	 * @deprecated Gives almost zero diagnostic information.
	 *
	 * Should only be used when it is known exactly from where this exception will be thrown. This is an unusual event, and this constructor is thereby deprecated.
	 */
	@Deprecated
	public InvalidWorldException(){
		super("An invalid world was specified.");
	}

	public InvalidWorldException(String message){
		super(message);
	}
}