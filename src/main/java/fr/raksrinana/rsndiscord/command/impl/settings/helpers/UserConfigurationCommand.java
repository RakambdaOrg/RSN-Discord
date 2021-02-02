package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;

public abstract class UserConfigurationCommand extends ValueConfigurationCommand<UserConfiguration>{
	protected UserConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected UserConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(event.getMessage().getMentionedUsers().isEmpty()){
			throw new IllegalArgumentException("Please mention the channel");
		}
		return new UserConfiguration(event.getMessage().getMentionedUsers().get(0));
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "User";
	}
}
