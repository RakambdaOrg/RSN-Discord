package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.anilist;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers.UserConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.newSettings.NewSettings;
import fr.mrcraftcod.gunterdiscord.newSettings.types.UserConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ThaUserConfigurationCommand extends UserConfigurationCommand{
	public ThaUserConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).getAniListConfiguration().setThaUser(null);
	}
	
	@Nonnull
	@Override
	protected Optional<UserConfiguration> getConfig(@Nonnull Guild guild){
		return NewSettings.getConfiguration(guild).getAniListConfiguration().getThaUser();
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull UserConfiguration value){
		NewSettings.getConfiguration(guild).getAniListConfiguration().setThaUser(value);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList Tha notification channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("thaChannel");
	}
}
