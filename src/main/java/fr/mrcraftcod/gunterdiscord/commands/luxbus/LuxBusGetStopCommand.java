package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusDeparture;
import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusStop;
import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusUtils;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
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
			else{
				askStop(event, stops);
			}
		}
		catch(final Exception e){
			LOGGER.error("", e);
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	private void askStop(final GuildMessageReceivedEvent event, final List<LuxBusStop> stops){
		if(stops.size() < 2){
			askLine(event, stops.get(0));
		}
		else{
			Actions.reply(event, message -> ReplyMessageListener.handleReply(new LuxBusStopSelectionWaitingReply(event, stops, message)), "Choose a stop:\n%s", IntStream.range(0, stops.size()).mapToObj(i -> String.format("%d: %s", i + 1, stops.get(i))).collect(Collectors.joining("\n")));
		}
	}
	
	public static void askLine(final GuildMessageReceivedEvent event, final LuxBusStop stop){
		try{
			final var departures = LuxBusUtils.getDepartures(stop);
			if(departures.stream().map(d -> d.getProduct().getLine()).distinct().count() < 2){
				if(departures.stream().map(LuxBusDeparture::getDirection).distinct().count() < 2){
					departures.stream().sorted().forEachOrdered(d -> Actions.reply(event, d.getAsEmbed(new EmbedBuilder()).build()));
				}
				else{
					askDirection(event, departures);
				}
			}
			else{
				Actions.reply(event, message -> ReplyMessageListener.handleReply(new LuxBusLineSelectionWaitingReply(event, departures, message)), "Choose a line:\n%s", departures.stream().map(d -> d.getProduct().getLine()).distinct().sorted().collect(Collectors.joining("\n")));
			}
		}
		catch(final Exception e){
			LOGGER.error("Failed to ask a bus line", e);
			Actions.reply(event, e.getLocalizedMessage());
		}
	}
	
	public static void askDirection(final GuildMessageReceivedEvent event, final List<LuxBusDeparture> filtered){
		final var directionDepartures = filtered.stream().collect(Collectors.groupingBy(LuxBusDeparture::getDirection));
		final var directionDeparturesNumbered = new HashMap<Integer, List<LuxBusDeparture>>();
		var i = 0;
		for(final var oldKey : directionDepartures.keySet()){
			directionDeparturesNumbered.put(++i, directionDepartures.get(oldKey));
		}
		Actions.reply(event, message -> ReplyMessageListener.handleReply(new LuxBusDirectionSelectionWaitingReply(event, directionDeparturesNumbered, message)), "Choose a direction:\n%s", directionDeparturesNumbered.keySet().stream().map(k -> String.format("%s: %s", k, directionDeparturesNumbered.get(k).stream().findFirst().map(LuxBusDeparture::getDirection).orElse("???"))).sorted().collect(Collectors.joining("\n")));
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
		return "Request buses at a stop";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("stop", "s");
	}
	
	@Override
	public String getDescription(){
		return "Get the next buses at a stop";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
