package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.anilist.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.anilist.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.anilist.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.anilist.ThaUserConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class AniListConfigurationCommandComposite extends CommandComposite{
	public AniListConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new NotificationChannelConfigurationCommand(this));
		addSubCommand(new ThaChannelConfigurationCommand(this));
		addSubCommand(new ThaUserConfigurationCommand(this));
		addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "AniList";
	}
	
	@NotNull
	@Override
	public @NotNull List<String> getCommandStrings(){
		return List.of("al", "anilist");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "AniList configurations";
	}
}
