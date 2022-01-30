package fr.raksrinana.rsndiscord.interaction.command;

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
	HANDLED,
	/**
	 * Indicates that no response has been sent.
	 */
	HANDLED_NO_MESSAGE,
	/**
	 * Will send a predefined message.
	 */
	NOT_ALLOWED,
	/**
	 * Command is not defined.
	 */
	NOT_IMPLEMENTED;
}
