package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.reactions.AutoTodoChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.reactions.SavedForwardingChannelsConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class ReactionsConfigurationCommandComposite extends CommandComposite{
	public ReactionsConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new AutoTodoChannelsConfigurationCommand(this));
		this.addSubCommand(new SavedForwardingChannelsConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
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
