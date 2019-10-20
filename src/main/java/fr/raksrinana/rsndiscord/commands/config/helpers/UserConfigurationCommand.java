package fr.raksrinana.rsndiscord.commands.config.helpers;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;

public abstract class UserConfigurationCommand extends ValueConfigurationCommand<UserConfiguration>{
	protected UserConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	protected UserConfiguration extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(event.getMessage().getMentionedUsers().isEmpty()){
			throw new IllegalArgumentException("Please mention the channel");
		}
		return new UserConfiguration(event.getMessage().getMentionedUsers().get(0));
	}
	
	@Override
	protected String getValueName(){
		return "User";
	}
}
