package fr.raksrinana.rsndiscord.command2.impl.bot;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command2.permission.CreatorPermission.CREATOR_PERMISSION;

@Log4j2
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
		log.info("BOT STOPPING");
		JDAWrappers.edit(event, "Stopping").submit();
		Main.close();
		return HANDLED;
	}
}
