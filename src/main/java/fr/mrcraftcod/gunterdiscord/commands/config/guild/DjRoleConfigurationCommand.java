package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DjRoleConfigurationCommand extends RoleConfigurationCommand{
	public DjRoleConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<RoleConfiguration> getConfig(@Nonnull final Guild guild){
		return NewSettings.getConfiguration(guild).getDjRole();
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final RoleConfiguration value){
		NewSettings.getConfiguration(guild).setDjRole(value);
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).setDjRole(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "DJ Role";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("djRole");
	}
}
