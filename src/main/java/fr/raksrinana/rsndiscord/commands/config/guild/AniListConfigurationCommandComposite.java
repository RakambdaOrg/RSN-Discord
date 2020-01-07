package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.anilist.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.anilist.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.anilist.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.anilist.ThaUserConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class AniListConfigurationCommandComposite extends CommandComposite{
	public AniListConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new ThaChannelConfigurationCommand(this));
		this.addSubCommand(new ThaUserConfigurationCommand(this));
		this.addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("al", "anilist");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "AniList configurations";
	}
}
