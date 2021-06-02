package fr.raksrinana.rsndiscord.command2.impl.bot;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command2.permission.CreatorPermission.CREATOR_PERMISSION;

public class StopCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "stop";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Stops the bot";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return CREATOR_PERMISSION;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		Log.getLogger(event.getGuild()).info("BOT STOPPING");
		JDAWrappers.edit(event, "Stopping").submit();
		Main.close();
		return SUCCESS;
	}
}
