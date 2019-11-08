package fr.raksrinana.rsndiscord.commands.luxbus;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusDeparture;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusStop;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@BotCommand
public class LuxBusGetStopCommand extends BasicCommand{
	private static final int PER_PAGE = 25;
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Stop", "The name of the stop", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		try{
			if(args.size() < 1){
				final var embedBuilder = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Invalid parameters");
				embedBuilder.addField("Reason", "Please provide a stop name", false);
				Actions.reply(event, embedBuilder.build());
				return CommandResult.SUCCESS;
			}
			final var stops = LuxBusUtils.searchStopByName(String.join(" ", args));
			if(stops.isEmpty()){
				final var embed = Utilities.buildEmbed(event.getAuthor(), Color.RED, "Command failed");
				embed.addField("Reason", "Your query didn't match any stop", false);
				Actions.reply(event, embed.build());
			}
			else{
				this.askStop(event, stops);
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
		return CommandResult.SUCCESS;
	}
	
	private void askStop(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final List<LuxBusStop> stops){
		if(stops.size() < 2){
			askLine(event, stops.get(0));
		}
		else if(stops.size() > 100){
			Actions.reply(event, "More than 100 stops matching, please give a better name for the stop.");
		}
		else{
			Actions.reply(event, "Choose a stop:");
			final var replyHandler = new LuxBusStopSelectionWaitingReply(event, stops);
			ReplyMessageListener.handleReply(replyHandler);
			IntStream.range(0, stops.size()).boxed().collect(Collectors.groupingBy(index -> index / PER_PAGE)).values().stream().map(indices -> indices.stream().map(stops::get).collect(Collectors.toList())).forEach(stopsBatch -> Actions.reply(event, replyHandler::addMessage, "%s", stopsBatch.stream().map(stop -> String.format("%d: %s", stops.indexOf(stop) + 1, stop)).collect(Collectors.joining("\n"))));
		}
	}
	
	static void askLine(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LuxBusStop stop){
		try{
			final var departures = LuxBusUtils.getDepartures(stop);
			if(departures.stream().map(departure -> departure.getProduct().getLine()).distinct().count() < 2){
				if(departures.stream().map(LuxBusDeparture::getDirection).distinct().count() < 2){
					departures.stream().sorted().forEachOrdered(departure -> Actions.reply(event, departure.getAsEmbed(Utilities.buildEmbed(event.getAuthor(), null, null)).build()));
				}
				else{
					askDirection(event, departures);
				}
			}
			else{
				Actions.reply(event, message -> ReplyMessageListener.handleReply(new LuxBusLineSelectionWaitingReply(event, departures, message)), "Choose a line:\n%s", departures.stream().map(departure -> departure.getProduct().getLine()).distinct().sorted().collect(Collectors.joining("\n")));
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Failed to ask a bus line", e);
			Actions.reply(event, e.getLocalizedMessage());
		}
	}
	
	static void askDirection(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final List<LuxBusDeparture> filtered){
		final var directionDepartures = filtered.stream().collect(Collectors.groupingBy(LuxBusDeparture::getDirection));
		final var directionDeparturesNumbered = new HashMap<Integer, List<LuxBusDeparture>>();
		var i = 0;
		for(final var luxBusDepartures : directionDepartures.values()){
			directionDeparturesNumbered.put(++i, luxBusDepartures);
		}
		Actions.reply(event, message -> ReplyMessageListener.handleReply(new LuxBusDirectionSelectionWaitingReply(event, directionDeparturesNumbered, message)), "Choose a direction:\n%s", directionDeparturesNumbered.keySet().stream().map(k -> String.format("%s: %s", k, directionDeparturesNumbered.get(k).stream().findFirst().map(LuxBusDeparture::getDirection).orElse("???"))).sorted().collect(Collectors.joining("\n")));
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <stop>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Request buses at a stop";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("luxbus", "bus");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Get the next buses at a stop";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
