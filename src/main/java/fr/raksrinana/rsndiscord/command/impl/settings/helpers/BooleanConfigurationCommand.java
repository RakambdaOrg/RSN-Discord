package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

@Log4j2
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
				log.error("Failed to parse boolean", e);
			}
		}
		throw new IllegalArgumentException("Please give a boolean value");
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Boolean";
	}
}
