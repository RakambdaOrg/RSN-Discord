package fr.raksrinana.rsndiscord.commands.config.guild.anilist;

import fr.raksrinana.rsndiscord.commands.config.helpers.UserConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ThaUserConfigurationCommand extends UserConfigurationCommand{
	public ThaUserConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<UserConfiguration> getConfig(@Nonnull final Guild guild){
		return Settings.getConfiguration(guild).getAniListConfiguration().getThaUser();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final UserConfiguration value){
		Settings.getConfiguration(guild).getAniListConfiguration().setThaUser(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		Settings.getConfiguration(guild).getAniListConfiguration().setThaUser(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList Tha notification channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("thaUser");
	}
}
