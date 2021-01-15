package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;

public abstract class StringConfigurationCommand extends ValueConfigurationCommand<String>{
	protected StringConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected String extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(!args.isEmpty()){
			return args.pop();
		}
		throw new IllegalArgumentException("Please give a string value");
	}
	
	@Override
	protected String getValueName(){
		return "String";
	}
}
