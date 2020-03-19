package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.commands.generic.NotAllowedException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class StopCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(Utilities.isCreator(event.getAuthor())){
			Main.close();
			Log.getLogger(event.getGuild()).info("BOT STOPPING");
		}
		else{
			throw new NotAllowedException("You're not the creator of the bot");
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Stop";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stop", "quit");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Stops the bot";
	}
}
