package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.externaltodos.EndpointConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.externaltodos.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.externaltodos.TokenConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class ExternalTodosConfigurationCommandComposite extends CommandComposite{
	public ExternalTodosConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new EndpointConfigurationCommand(this));
		this.addSubCommand(new TokenConfigurationCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "External todos";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("externalTodos");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "External todos configurations";
	}
}
