package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class LeaverRoleConfigurationCommand extends RoleConfigurationCommand{
	public LeaverRoleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		Settings.get(guild).setLeaverRole(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setLeaverRole(null);
	}
	
	@NonNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getLeaverRole();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Leaver Role";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("leaverRole");
	}
}
