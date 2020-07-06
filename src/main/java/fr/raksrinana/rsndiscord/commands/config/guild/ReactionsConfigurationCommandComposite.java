package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.reactions.AutoTodoChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.reactions.SavedForwardingChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;

public class ReactionsConfigurationCommandComposite extends CommandComposite{
	public ReactionsConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new AutoTodoChannelsConfigurationCommand(this));
		this.addSubCommand(new SavedForwardingChannelsConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Reactions";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("reactions");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Reactions configurations";
	}
}
