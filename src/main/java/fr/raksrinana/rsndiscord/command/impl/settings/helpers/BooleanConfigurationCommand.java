package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

public abstract class BooleanConfigurationCommand extends ValueConfigurationCommand<Boolean>{
	protected BooleanConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected Boolean extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(!args.isEmpty()){
			try{
				return Boolean.valueOf(args.pop());
			}
			catch(Exception e){
				Log.getLogger(event.getGuild()).error("Failed to parse boolean", e);
			}
		}
		throw new IllegalArgumentException("Please give a boolean value");
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Boolean";
	}
}
