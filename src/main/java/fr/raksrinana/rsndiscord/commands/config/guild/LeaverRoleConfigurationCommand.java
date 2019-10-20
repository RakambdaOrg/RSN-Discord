package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class LeaverRoleConfigurationCommand extends RoleConfigurationCommand{
	public LeaverRoleConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<RoleConfiguration> getConfig(@Nonnull final Guild guild){
		return NewSettings.getConfiguration(guild).getLeaverRole();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final RoleConfiguration value){
		NewSettings.getConfiguration(guild).setLeaverRole(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).setLeaverRole(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Leaver Role";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("leaverRole");
	}
}
