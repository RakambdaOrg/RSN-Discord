package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.commands.generic.NotAllowedException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class StopCommand extends BasicCommand{
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(Utilities.isCreator(event.getMember())){
			Main.close();
			Log.getLogger(event.getGuild()).info("BOT STOPPING");
		}
		else{
			throw new NotAllowedException("You're not the creator of the bot");
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Stop";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stop", "quit");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Stops the bot";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
