package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.randomkick.KickedRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.randomkick.RandomKickRolesPingRolesConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;

public class RandomKickConfigurationCommandComposite extends CommandComposite{
	public RandomKickConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new RandomKickRolesPingRolesConfigurationCommand(this));
		this.addSubCommand(new KickedRoleConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "RandomKick";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("randomKick");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Random kick configuration";
	}
}
