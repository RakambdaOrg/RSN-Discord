package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.config.helpers.BooleanConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class IrcForwardConfigurationCommand extends BooleanConfigurationCommand{
	public IrcForwardConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Boolean> getConfig(Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getIrcForward());
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull Boolean value){
		NewSettings.getConfiguration(guild).setIrcForward(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
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
