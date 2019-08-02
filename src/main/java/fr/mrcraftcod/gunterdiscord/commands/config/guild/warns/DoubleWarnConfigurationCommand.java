package fr.mrcraftcod.gunterdiscord.commands.config.guild.warns;

import fr.mrcraftcod.gunterdiscord.commands.config.helpers.WarnConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.guild.warns.WarnConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class DoubleWarnConfigurationCommand extends WarnConfigurationCommand{
	public DoubleWarnConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<WarnConfiguration> getConfig(final Guild guild){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getDoubleWarn();
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).getWarnsConfiguration().setDoubleWarn(null);
	}
	
	@Override
	protected void createConfig(@Nonnull final Guild guild, @Nonnull final Role role, final long delay){
		NewSettings.getConfiguration(guild).getWarnsConfiguration().setDoubleWarn(new WarnConfiguration(role, delay));
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Double warn";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("doubleWarn");
	}
}
