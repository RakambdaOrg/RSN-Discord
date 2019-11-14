package fr.raksrinana.rsndiscord.utils;

/**
 * Exception used when an invalid response is received from an HTTP request.
 */
public class InvalidResponseException extends RuntimeException{
	/**
	 * @param reason The reason.
	 */
	public InvalidResponseException(String reason){
		super(reason);
	}
}
