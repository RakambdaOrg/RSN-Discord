package fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.UserConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ThaUserConfigurationCommand extends UserConfigurationCommand{
	public ThaUserConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull UserConfiguration value){
		Settings.get(guild).getTraktConfiguration().setThaUser(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getTraktConfiguration().setThaUser(null);
	}
	
	@NotNull
	@Override
	protected Optional<UserConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getTraktConfiguration().getThaUser();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Trakt Tha notification channel";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("thaUser");
	}
}
