package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.questions.InputChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.questions.OutputChannelConfigurationCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class QuestionsConfigurationCommandComposite extends CommandComposite{
	public QuestionsConfigurationCommandComposite(@Nullable Command parent){
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
