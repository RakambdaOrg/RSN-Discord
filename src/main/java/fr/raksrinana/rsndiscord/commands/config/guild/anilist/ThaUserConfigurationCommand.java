package fr.raksrinana.rsndiscord.commands.config.guild.anilist;

import fr.raksrinana.rsndiscord.commands.config.helpers.UserConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class ThaUserConfigurationCommand extends UserConfigurationCommand{
	public ThaUserConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final UserConfiguration value){
		Settings.get(guild).getAniListConfiguration().setThaUser(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getAniListConfiguration().setThaUser(null);
	}
	
	@NonNull
	@Override
	protected Optional<UserConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getAniListConfiguration().getThaUser();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList Tha notification channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("thaUser");
	}
}
