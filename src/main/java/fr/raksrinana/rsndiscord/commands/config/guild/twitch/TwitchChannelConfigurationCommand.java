package fr.raksrinana.rsndiscord.commands.config.guild.twitch;

import fr.raksrinana.rsndiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class TwitchChannelConfigurationCommand extends ChannelConfigurationCommand{
	public TwitchChannelConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration value){
		Settings.get(guild).getTwitchConfiguration().setTwitchChannel(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getTwitchConfiguration().setTwitchChannel(null);
	}
	
	@NonNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getTwitchConfiguration().getTwitchChannel();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Twitch channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("channel");
	}
}
