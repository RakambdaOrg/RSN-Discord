package fr.mrcraftcod.gunterdiscord.commands.config.guild.anilist;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class NotificationChannelConfigurationCommand extends ChannelConfigurationCommand{
	public NotificationChannelConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).getAniListConfiguration().setNotificationsChannel(null);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull Guild guild){
		return NewSettings.getConfiguration(guild).getAniListConfiguration().getNotificationsChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getAniListConfiguration().setNotificationsChannel(value);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList notification channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("notificationChannel");
	}
}
