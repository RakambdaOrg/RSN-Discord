package fr.raksrinana.rsndiscord.utils;

/**
 * Exception used when an invalid response is received from an HTTP request.
 */
public class InvalidResponseException extends RuntimeException{
	private static final long serialVersionUID = -8781299726434309433L;
	
	/**
	 * @param reason The reason.
	 */
	public InvalidResponseException(String reason){
		super(reason);
	}
}
