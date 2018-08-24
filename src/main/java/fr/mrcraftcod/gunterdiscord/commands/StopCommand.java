package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class StopCommand extends BasicCommand{
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(Utilities.isCreator(event.getMember())){
			Main.close();
			event.getJDA().shutdown();
			getLogger(event.getGuild()).info("BOT STOPPING");
		}
		else{
			return CommandResult.NOT_ALLOWED;
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Stop";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("stop");
	}
	
	@Override
	public String getDescription(){
		return "Arrête le bot";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
