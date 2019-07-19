package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.config.guild.anilist.NotificationChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.config.guild.anilist.ThaChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.config.guild.anilist.ThaUserConfigurationCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AniListConfigurationCommandComposite extends CommandComposite{
	public AniListConfigurationCommandComposite(@Nullable Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
		this.addSubCommand(new ThaChannelConfigurationCommand(this));
		this.addSubCommand(new ThaUserConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("al", "anilist");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "AniList configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
