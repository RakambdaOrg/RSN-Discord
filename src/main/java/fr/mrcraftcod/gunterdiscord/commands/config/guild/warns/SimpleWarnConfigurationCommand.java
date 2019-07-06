package fr.mrcraftcod.gunterdiscord.commands.config.guild.warns;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.WarnConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.guild.warns.WarnConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class SimpleWarnConfigurationCommand extends WarnConfigurationCommand{
	public SimpleWarnConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<WarnConfiguration> getConfig(Guild guild){
		return NewSettings.getConfiguration(guild).getWarnsConfiguration().getSimpleWarn();
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).getWarnsConfiguration().setSimpleWarn(null);
	}
	
	@Override
	protected void createConfig(@Nonnull Guild guild, @Nonnull Role role, long delay){
		NewSettings.getConfiguration(guild).getWarnsConfiguration().setSimpleWarn(new WarnConfiguration(role, delay));
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Simple warn";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("simpleWarn");
	}
}
