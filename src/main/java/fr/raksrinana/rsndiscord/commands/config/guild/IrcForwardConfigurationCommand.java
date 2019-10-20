package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.BooleanConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class IrcForwardConfigurationCommand extends BooleanConfigurationCommand{
	public IrcForwardConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Boolean> getConfig(final Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getIrcForward());
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final Boolean value){
		NewSettings.getConfiguration(guild).setIrcForward(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).setIrcForward(false);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "IRC message forwarding";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ircForward");
	}
}
