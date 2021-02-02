package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

public abstract class StringConfigurationCommand extends ValueConfigurationCommand<String>{
	protected StringConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected String extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(!args.isEmpty()){
			return args.pop();
		}
		throw new IllegalArgumentException("Please give a string value");
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "String";
	}
}
