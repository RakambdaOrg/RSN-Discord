package fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope;

import fr.raksrinana.rsndiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class PosterRoleConfigurationCommand extends RoleConfigurationCommand{
	public PosterRoleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		Settings.get(guild).getTrombinoscope().setPosterRole(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getTrombinoscope().setPosterRole(null);
	}
	
	@NonNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getTrombinoscope().getPosterRole();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Poster role";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("posterRole");
	}
}
