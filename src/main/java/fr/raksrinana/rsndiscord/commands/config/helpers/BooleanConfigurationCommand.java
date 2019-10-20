package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public abstract class BooleanConfigurationCommand extends ValueConfigurationCommand<Boolean>{
	protected BooleanConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	protected String getValueName(){
		return "Boolean";
	}
	
	@Override
	protected Boolean extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
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
}
