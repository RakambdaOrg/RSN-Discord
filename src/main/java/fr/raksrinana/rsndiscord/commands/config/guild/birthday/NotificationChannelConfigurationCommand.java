package fr.raksrinana.rsndiscord.commands.config.guild.birthday;

import fr.raksrinana.rsndiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class NotificationChannelConfigurationCommand extends ChannelConfigurationCommand{
	public NotificationChannelConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration value){
		Settings.get(guild).getBirthdays().setNotificationChannel(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getBirthdays().setNotificationChannel(null);
	}
	
	@NonNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getBirthdays().getNotificationChannel();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Notification channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("notificationChannel");
	}
}
