package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.CREATOR;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class StopCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		Main.close();
		Log.getLogger(event.getGuild()).info("BOT STOPPING");
		return SUCCESS;
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return CREATOR;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.stop.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stop", "quit");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.stop.description");
	}
}
