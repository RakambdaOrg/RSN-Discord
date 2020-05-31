package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.externaltodos.EndpointConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.externaltodos.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.externaltodos.TokenConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class ExternalTodosConfigurationCommandComposite extends CommandComposite{
	public ExternalTodosConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new EndpointConfigurationCommand(this));
		this.addSubCommand(new TokenConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.CREATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "External todos";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("externalTodos");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "External todos configurations";
	}
}
