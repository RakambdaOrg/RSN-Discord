package fr.mrcraftcod.gunterdiscord.commands.overwatch;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.reply.EmoteWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.OverwatchUtils;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.OverwatchStage;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week.OverwatchWeek;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class OverwatchGetMatchCommand extends BasicCommand{
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchMatch> onMatch = (event, match) -> Actions.reply(event, match.buildEmbed(event.getUser()).build());
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchWeek> onWeek = (event, week) -> {
		if(!week.getMatches().isEmpty()){
			if(week.getMatches().size() == 1){
				onMatch.accept(event, week.getMatches().get(0));
			}
			else{
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchMatch>();
				final var currentMatch = week.getCurrentMatch();
				final var nextMatch = week.getNextMatch();
				final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available matches");
				week.getMatches().forEach(m -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, m);
					builder.addField(emote.getValue() + ": " + m.getVsCompetitorsNames(), currentMatch.map(m::equals).orElse(false) ? "Current" : (nextMatch.map(m::equals).orElse(false) ? "Next" : ""), false);
				});
				builder.setFooter("ID: " + week.getId());
				Actions.reply(event, message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> message.addReaction(e.getValue()).queue());
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, OverwatchGetMatchCommand.onMatch));
				}, builder.build());
			}
		}
		else{
			Actions.reply(event, "No matches found");
		}
	};
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchStage> onStage = (event, stage) -> {
		if(!stage.getWeeks().isEmpty()){
			if(stage.getWeeks().size() == 1){
				onWeek.accept(event, stage.getWeeks().get(0));
			}
			else{
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchWeek>();
				final var currentWeek = stage.getCurrentWeek();
				final var nextWeek = stage.getNextWeek();
				final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available weeks");
				stage.getWeeks().forEach(w -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, w);
					builder.addField(emote.getValue() + ": " + w.getName(), currentWeek.map(w::equals).orElse(false) ? "Current" : (nextWeek.map(w::equals).orElse(false) ? "Next" : ""), false);
				});
				builder.setFooter("ID: " + stage.getId());
				Actions.reply(event, message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> message.addReaction(e.getValue()).queue());
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, OverwatchGetMatchCommand.onWeek));
				}, builder.build());
			}
		}
		else{
			Actions.reply(event, "No week found");
		}
	};
	
	public OverwatchGetMatchCommand(final Command parent){
		super(parent);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		OverwatchUtils.getLastResponse().ifPresentOrElse(overwatchResponse -> {
			if(!overwatchResponse.getData().getStages().isEmpty()){
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchStage>();
				final var currentStage = overwatchResponse.getData().getCurrentStage();
				final var nextStage = overwatchResponse.getData().getNextStage();
				final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Available stages");
				overwatchResponse.getData().getStages().forEach(s -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, s);
					builder.addField(emote.getValue() + ": " + s.getName(), currentStage.map(s::equals).orElse(false) ? "Current" : (nextStage.map(s::equals).orElse(false) ? "Next" : ""), false);
				});
				Actions.reply(event, message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> message.addReaction(e.getValue()).queue());
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getAuthor(), message, onStage));
				}, builder.build());
			}
			else{
				Actions.reply(event, "No stages found");
			}
		}, () -> Actions.reply(event, "Error while getting data from Overwatch"));
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Info match";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("m", "match");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Get the info of a match";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
