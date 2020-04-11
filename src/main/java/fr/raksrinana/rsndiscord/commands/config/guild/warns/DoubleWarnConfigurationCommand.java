package fr.raksrinana.rsndiscord.commands.config.guild.warns;

import fr.raksrinana.rsndiscord.commands.config.helpers.WarnConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import java.util.List;
import java.util.Optional;

public class DoubleWarnConfigurationCommand extends WarnConfigurationCommand{
	public DoubleWarnConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getWarnsConfiguration().setDoubleWarn(null);
	}
	
	@NonNull
	@Override
	protected Optional<WarnConfiguration> getConfig(final Guild guild){
		return Settings.get(guild).getWarnsConfiguration().getDoubleWarn();
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final Role role, final long delay){
		Settings.get(guild).getWarnsConfiguration().setDoubleWarn(new WarnConfiguration(role, delay));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Double warn";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("doubleWarn");
	}
}
