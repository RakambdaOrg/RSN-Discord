package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.config.guild.overwatch.NotificationChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class OverwatchConfigurationCommandComposite extends CommandComposite{
	public OverwatchConfigurationCommandComposite(@Nullable final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Overwatch";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ow", "overwatch");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Overwatch configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
