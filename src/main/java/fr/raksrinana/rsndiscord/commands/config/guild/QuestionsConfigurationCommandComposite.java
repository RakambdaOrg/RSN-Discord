package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.questions.InputChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.questions.OutputChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class QuestionsConfigurationCommandComposite extends CommandComposite{
	public QuestionsConfigurationCommandComposite(@Nullable final Command parent){
		super(parent);
		this.addSubCommand(new InputChannelConfigurationCommand(this));
		this.addSubCommand(new OutputChannelConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Questions";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("questions");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Questions configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
