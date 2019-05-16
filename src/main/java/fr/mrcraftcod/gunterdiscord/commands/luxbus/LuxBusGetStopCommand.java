package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusUtils;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Color;
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
			if(args.size() < 1){
				final var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
				embed.addField("Reason", "Please provide a stop name", false);
				Actions.reply(event, embed.build());
				return CommandResult.SUCCESS;
			}
			final var stops = LuxBusUtils.searchStopByName(String.join(" ", args));
			if(stops.isEmpty()){
				final var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Command failed");
				embed.addField("Reason", "Your query didn't match any stop", false);
				Actions.reply(event, embed.build());
			}
			else if(stops.size() > 1){
				final var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Command failed");
				embed.addField("Reason", "There was several stops matching your query", false);
				Actions.reply(event, embed.build());
				stops.forEach(s -> Actions.reply(event, s.getName()));
			}
			else{
				final var stop = stops.get(0);
				Actions.reply(event, "Displaying stops for stop %s", stop);
				LuxBusUtils.getDepartures(stop).stream().sorted().forEachOrdered(dep -> Actions.reply(event, dep.getAsEmbed(new EmbedBuilder()).build()));
			}
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
