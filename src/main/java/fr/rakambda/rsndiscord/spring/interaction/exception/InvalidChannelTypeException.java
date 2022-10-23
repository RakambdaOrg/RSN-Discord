package fr.rakambda.rsndiscord.spring.interaction.exception;

import net.dv8tion.jda.api.entities.channel.ChannelType;

public class InvalidChannelTypeException extends InteractionException{
	private final ChannelType type;
	
	public InvalidChannelTypeException(ChannelType type){
		super("Invalid channel type " + type);
		this.type = type;
	}
}
