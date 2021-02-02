package fr.raksrinana.rsndiscord.command.impl.settings.guild.anilist;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class MediaChangeChannelConfigurationCommand extends ChannelConfigurationCommand{
	public MediaChangeChannelConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull ChannelConfiguration value){
		Settings.get(guild).getAniListConfiguration().setMediaChangeChannel(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getAniListConfiguration().setMediaChangeChannel(null);
	}
	
	@NotNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getAniListConfiguration().getMediaChangeChannel();
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.config.guild.anilist.media-change-channel.name");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("mediaChangeChannel");
	}
}
