package fr.raksrinana.rsndiscord.commands.config.guild.randomkick;

import fr.raksrinana.rsndiscord.commands.config.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;

public class KickedRoleConfigurationCommand extends RoleConfigurationCommand{
	public KickedRoleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		Settings.get(guild).getRandomKick().setKickedRole(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getRandomKick().setKickedRole(null);
	}
	
	@NonNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getRandomKick().getKickedRole();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Kicked role";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("kickedRole");
	}
}
