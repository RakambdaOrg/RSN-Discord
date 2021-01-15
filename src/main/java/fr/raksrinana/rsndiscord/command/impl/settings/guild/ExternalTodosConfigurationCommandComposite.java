package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos.EndpointConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos.TokenConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ExternalTodosConfigurationCommandComposite extends CommandComposite{
	public ExternalTodosConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new EndpointConfigurationCommand(this));
		this.addSubCommand(new TokenConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
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
