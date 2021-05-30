package fr.raksrinana.rsndiscord.command;

public enum CommandResult{
	/**
	 * Will send a predefined response.
	 */
	BAD_ARGUMENTS,
	/**
	 * Will send an error message.
	 */
	FAILED,
	/**
	 * Indicates the command was successful but no message were sent.
	 */
	SUCCESS,
	/**
	 * Indicates that a response has been sent.
	 */
	REPLIED,
	/**
	 * Will send a predefined message.
	 */
	NOT_ALLOWED
}
