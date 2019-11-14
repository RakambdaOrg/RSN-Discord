package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;

public abstract class BooleanConfigurationCommand extends ValueConfigurationCommand<Boolean>{
	protected BooleanConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected Boolean extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(!args.isEmpty()){
			try{
				return Boolean.valueOf(args.pop());
			}
			catch(final Exception e){
				Log.getLogger(event.getGuild()).error("Failed to parse boolean", e);
			}
		}
		throw new IllegalArgumentException("Please give a boolean value");
	}
	
	@Override
	protected String getValueName(){
		return "Boolean";
	}
}
