package fr.raksrinana.rsndiscord.commands.config.guild.externaltodos;

import fr.raksrinana.rsndiscord.commands.config.helpers.StringConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class EndpointConfigurationCommand extends StringConfigurationCommand{
	public EndpointConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final String value){
		Settings.get(guild).getExternalTodos().setEndpoint(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getExternalTodos().setEndpoint(null);
	}
	
	@NonNull
	@Override
	protected Optional<String> getConfig(final Guild guild){
		return Settings.get(guild).getExternalTodos().getEndpoint();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Endpoint";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("endpoint");
	}
}
