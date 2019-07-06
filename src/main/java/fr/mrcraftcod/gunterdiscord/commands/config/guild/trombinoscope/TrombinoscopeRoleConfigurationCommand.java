package fr.mrcraftcod.gunterdiscord.commands.config.guild.trombinoscope;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TrombinoscopeRoleConfigurationCommand extends RoleConfigurationCommand{
	public TrombinoscopeRoleConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).getTrombinoscopeConfiguration().setParticipantRole(null);
	}
	
	@Nonnull
	@Override
	protected Optional<RoleConfiguration> getConfig(@Nonnull Guild guild){
		return NewSettings.getConfiguration(guild).getTrombinoscopeConfiguration().getParticipantRole();
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull RoleConfiguration value){
		NewSettings.getConfiguration(guild).getTrombinoscopeConfiguration().setParticipantRole(value);
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
