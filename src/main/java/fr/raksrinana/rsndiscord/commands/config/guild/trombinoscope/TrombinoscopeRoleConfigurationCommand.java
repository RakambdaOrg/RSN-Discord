package fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope;

import fr.raksrinana.rsndiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class TrombinoscopeRoleConfigurationCommand extends RoleConfigurationCommand{
	public TrombinoscopeRoleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		Settings.get(guild).getTrombinoscopeConfiguration().setParticipantRole(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getTrombinoscopeConfiguration().setParticipantRole(null);
	}
	
	@NonNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getTrombinoscopeConfiguration().getParticipantRole();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trombinoscope role channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participantRole");
	}
}
