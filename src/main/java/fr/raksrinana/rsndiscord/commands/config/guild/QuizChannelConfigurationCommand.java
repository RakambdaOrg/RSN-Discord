package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class QuizChannelConfigurationCommand extends ChannelConfigurationCommand{
	public QuizChannelConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull final Guild guild){
		return NewSettings.getConfiguration(guild).getQuizChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final ChannelConfiguration value){
		NewSettings.getConfiguration(guild).setQuizChannel(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).setQuizChannel(null);
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
