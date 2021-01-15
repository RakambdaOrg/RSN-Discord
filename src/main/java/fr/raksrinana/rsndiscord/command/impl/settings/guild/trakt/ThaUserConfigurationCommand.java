package fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.UserConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ThaUserConfigurationCommand extends UserConfigurationCommand{
	public ThaUserConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final UserConfiguration value){
		Settings.get(guild).getTraktConfiguration().setThaUser(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getTraktConfiguration().setThaUser(null);
	}
	
	@NonNull
	@Override
	protected Optional<UserConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getTraktConfiguration().getThaUser();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Trakt Tha notification channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("thaUser");
	}
}
