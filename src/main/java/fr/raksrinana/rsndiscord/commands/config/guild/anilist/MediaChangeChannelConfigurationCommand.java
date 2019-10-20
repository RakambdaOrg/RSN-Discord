package fr.raksrinana.rsndiscord.commands.config.guild.anilist;

import fr.raksrinana.rsndiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MediaChangeChannelConfigurationCommand extends ChannelConfigurationCommand{
	public MediaChangeChannelConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull final Guild guild){
		return NewSettings.getConfiguration(guild).getAniListConfiguration().getMediaChangeChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getAniListConfiguration().setMediaChangeChannel(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).getAniListConfiguration().setMediaChangeChannel(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList media change channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("mediaChangeChannel");
	}
}
