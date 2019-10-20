package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.participation.ReportChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.participation.UsersPinnedConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ParticipationConfigurationCommandComposite extends CommandComposite{
	public ParticipationConfigurationCommandComposite(@Nullable final Command parent){
		super(parent);
		this.addSubCommand(new UsersPinnedConfigurationCommand(this));
		this.addSubCommand(new ReportChannelConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Participation";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Participation configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
