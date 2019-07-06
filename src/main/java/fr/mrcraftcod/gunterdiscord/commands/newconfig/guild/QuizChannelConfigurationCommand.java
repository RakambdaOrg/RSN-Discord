package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers.ChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.newSettings.NewSettings;
import fr.mrcraftcod.gunterdiscord.newSettings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class QuizChannelConfigurationCommand extends ChannelConfigurationCommand{
	public QuizChannelConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).setQuizChannel(null);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull Guild guild){
		return NewSettings.getConfiguration(guild).getQuizChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		NewSettings.getConfiguration(guild).setQuizChannel(value);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Quiz channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("quizChannel");
	}
}
