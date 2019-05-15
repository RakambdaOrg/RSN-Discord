package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import java.util.List;

public class LuxBusGetStopCommand extends BasicCommand{
	public static final Logger LOGGER = LoggerFactory.getLogger(LuxBusGetStopCommand.class);
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	LuxBusGetStopCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		try{
			Actions.reply(event, String.join("\n", LuxBusUtils.getStopIDs()));
		}
		catch(Exception e){
			LOGGER.error("", e);
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage();
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Request busses at a stop";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("stop", "s");
	}
	
	@Override
	public String getDescription(){
		return "Get the next busses at a stop";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
