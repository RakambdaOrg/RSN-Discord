package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.warns;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers.WarnConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.newSettings.NewSettings;
import fr.mrcraftcod.gunterdiscord.newSettings.guild.warns.WarnConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class DoubleWarnConfigurationCommand extends WarnConfigurationCommand{
	public DoubleWarnConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<WarnConfiguration> getConfig(Guild guild){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getDoubleWarn();
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).getWarnsConfiguration().setDoubleWarn(null);
	}
	
	@Override
	protected void createConfig(@Nonnull Guild guild, @Nonnull Role role, long delay){
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
