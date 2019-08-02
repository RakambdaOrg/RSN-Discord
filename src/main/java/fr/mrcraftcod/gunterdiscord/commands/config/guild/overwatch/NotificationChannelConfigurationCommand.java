package fr.mrcraftcod.gunterdiscord.commands.config.guild.overwatch;

import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class NotificationChannelConfigurationCommand extends ChannelConfigurationCommand{
	public NotificationChannelConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull final Guild guild){
		return NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().getNotificationChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().setNotificationChannel(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().setNotificationChannel(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Notification channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("notificationChannel");
	}
}
