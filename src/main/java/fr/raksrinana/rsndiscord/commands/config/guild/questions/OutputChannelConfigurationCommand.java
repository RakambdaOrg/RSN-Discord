package fr.raksrinana.rsndiscord.commands.config.guild.questions;

import fr.raksrinana.rsndiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class OutputChannelConfigurationCommand extends ChannelConfigurationCommand{
	public OutputChannelConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull final Guild guild){
		return Settings.getConfiguration(guild).getQuestionsConfiguration().getOutputChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final ChannelConfiguration value){
		Settings.getConfiguration(guild).getQuestionsConfiguration().setOutputChannel(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		Settings.getConfiguration(guild).getQuestionsConfiguration().setOutputChannel(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Output channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("outputChannel");
	}
}
