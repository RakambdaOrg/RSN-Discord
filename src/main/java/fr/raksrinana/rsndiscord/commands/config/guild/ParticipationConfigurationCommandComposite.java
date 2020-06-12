package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.participation.IgnoredChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.participation.ReportChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;

public class ParticipationConfigurationCommandComposite extends CommandComposite{
	public ParticipationConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new IgnoredChannelsConfigurationCommand(this));
		this.addSubCommand(new ReportChannelConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Participation";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Participation configurations";
	}
}
