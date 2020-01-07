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

public class MegaWarnConfigurationCommand extends WarnConfigurationCommand{
	public MegaWarnConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final Role role, final long delay){
		Settings.get(guild).getWarnsConfiguration().setMegaWarn(new WarnConfiguration(role, delay));
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getWarnsConfiguration().setMegaWarn(null);
	}
	
	@NonNull
	@Override
	protected Optional<WarnConfiguration> getConfig(final Guild guild){
		return Settings.get(guild).getWarnsConfiguration().getMegaWarn();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Mega warn";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("megaWarn");
	}
}
