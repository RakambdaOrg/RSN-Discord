package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.runners.DisplayDailyStatsScheduledRunner;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class TempCommand extends BasicCommand{
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		new DisplayDailyStatsScheduledRunner(event.getJDA()).run();
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.CREATOR;
	}
	
	@Override
	public String getName(){
		return "Temporary command";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("temp");
	}
	
	@Override
	public String getDescription(){
		return "For tests";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
