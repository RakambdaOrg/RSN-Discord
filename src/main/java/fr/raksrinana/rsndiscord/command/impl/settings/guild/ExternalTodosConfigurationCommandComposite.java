package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos.EndpointConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos.TokenConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ExternalTodosConfigurationCommandComposite extends CommandComposite{
	public ExternalTodosConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new NotificationChannelConfigurationCommand(this));
		addSubCommand(new EndpointConfigurationCommand(this));
		addSubCommand(new TokenConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "External todos";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("externalTodos");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "External todos configurations";
	}
}
