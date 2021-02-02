package fr.raksrinana.rsndiscord.command.impl.settings.guild.externaltodos;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.StringConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TokenConfigurationCommand extends StringConfigurationCommand{
	public TokenConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull String value){
		Settings.get(guild).getExternalTodos().setToken(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getExternalTodos().setToken(null);
	}
	
	@NotNull
	@Override
	protected Optional<String> getConfig(Guild guild){
		return Settings.get(guild).getExternalTodos().getToken();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Token";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("token");
	}
}
