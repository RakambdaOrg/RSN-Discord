package fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope;

import fr.raksrinana.rsndiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TrombinoscopeRoleConfigurationCommand extends RoleConfigurationCommand{
	public TrombinoscopeRoleConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<RoleConfiguration> getConfig(@Nonnull final Guild guild){
		return Settings.getConfiguration(guild).getTrombinoscopeConfiguration().getParticipantRole();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final RoleConfiguration value){
		Settings.getConfiguration(guild).getTrombinoscopeConfiguration().setParticipantRole(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		Settings.getConfiguration(guild).getTrombinoscopeConfiguration().setParticipantRole(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Trombinoscope role channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participantRole");
	}
}
