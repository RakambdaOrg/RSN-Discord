package fr.raksrinana.rsndiscord.commands.config.guild.anilist;

import fr.raksrinana.rsndiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class MediaChangeChannelConfigurationCommand extends ChannelConfigurationCommand{
	public MediaChangeChannelConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final ChannelConfiguration value){
		Settings.get(guild).getAniListConfiguration().setMediaChangeChannel(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getAniListConfiguration().setMediaChangeChannel(null);
	}
	
	@NonNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getAniListConfiguration().getMediaChangeChannel();
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.config.guild.anilist.media-change-channel.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("mediaChangeChannel");
	}
}
