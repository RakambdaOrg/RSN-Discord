package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;

public abstract class UserConfigurationCommand extends ValueConfigurationCommand<UserConfiguration>{
	protected UserConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected String getValueName(){
		return "User";
	}
	
	@Override
	protected UserConfiguration extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(event.getMessage().getMentionedUsers().isEmpty()){
			throw new IllegalArgumentException("Please mention the channel");
		}
		return new UserConfiguration(event.getMessage().getMentionedUsers().get(0));
	}
}
