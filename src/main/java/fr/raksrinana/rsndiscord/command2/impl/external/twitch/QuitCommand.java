package fr.raksrinana.rsndiscord.command2.impl.external.twitch;

import fr.raksrinana.rsndiscord.api.twitch.TwitchUtils;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.command2.permission.CreatorPermission.CREATOR_PERMISSION;

public class QuitCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "quit";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Disconnects from all twitch channels";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return CREATOR_PERMISSION;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		TwitchUtils.disconnectAll();
		return HANDLED_NO_MESSAGE;
	}
}
