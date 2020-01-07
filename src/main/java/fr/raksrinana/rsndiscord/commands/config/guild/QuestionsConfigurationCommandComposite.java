package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.questions.InputChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.questions.OutputChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class QuestionsConfigurationCommandComposite extends CommandComposite{
	public QuestionsConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new InputChannelConfigurationCommand(this));
		this.addSubCommand(new OutputChannelConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Questions";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("questions");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Questions configurations";
	}
}
