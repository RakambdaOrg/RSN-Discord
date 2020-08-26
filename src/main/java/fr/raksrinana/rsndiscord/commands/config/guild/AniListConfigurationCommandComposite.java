package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.anilist.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.anilist.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.anilist.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.anilist.ThaUserConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class AniListConfigurationCommandComposite extends CommandComposite{
	public AniListConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new ThaChannelConfigurationCommand(this));
		this.addSubCommand(new ThaUserConfigurationCommand(this));
		this.addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "AniList";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("al", "anilist");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "AniList configurations";
	}
}
