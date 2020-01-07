package fr.raksrinana.rsndiscord.commands.overwatch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.reply.EmoteWaitingUserReply;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.overwatch.OverwatchUtils;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.OverwatchStage;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.week.OverwatchWeek;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class OverwatchGetMatchCommand extends BasicCommand{
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchMatch> onMatch = (event, match) -> Actions.reply(event, "", match.buildEmbed(event.getUser()).build());
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
				final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available matches", null);
				week.getMatches().forEach(m -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, m);
					builder.addField(emote.getValue() + ": " + m.getVsCompetitorsNames(), currentMatch.map(m::equals).orElse(false) ? "Current" : (nextMatch.map(m::equals).orElse(false) ? "Next" : ""), false);
				});
				builder.setFooter("ID: " + week.getId());
				Actions.reply(event, "", builder.build()).thenAccept(message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, OverwatchGetMatchCommand.onMatch));
				});
			}
		}
		else{
			Actions.reply(event, "No matches found", null);
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
				final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available weeks", null);
				stage.getWeeks().forEach(w -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, w);
					builder.addField(emote.getValue() + ": " + w.getName(), currentWeek.map(w::equals).orElse(false) ? "Current" : (nextWeek.map(w::equals).orElse(false) ? "Next" : ""), false);
				});
				builder.setFooter("ID: " + stage.getId());
				Actions.reply(event, "", builder.build()).thenAccept(message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, OverwatchGetMatchCommand.onWeek));
				});
			}
		}
		else{
			Actions.reply(event, "No week found", null);
		}
	};
	
	public OverwatchGetMatchCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		OverwatchUtils.getData().ifPresentOrElse(overwatchResponse -> {
			if(!overwatchResponse.getData().getStages().isEmpty()){
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchStage>();
				final var currentStage = overwatchResponse.getData().getCurrentStage();
				final var nextStage = overwatchResponse.getData().getNextStage();
				final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Available stages", null);
				overwatchResponse.getData().getStages().forEach(s -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, s);
					builder.addField(emote.getValue() + ": " + s.getName(), currentStage.map(s::equals).orElse(false) ? "Current" : (nextStage.map(s::equals).orElse(false) ? "Next" : ""), false);
				});
				Actions.reply(event, "", builder.build()).thenAccept(message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getAuthor(), message, onStage));
				});
			}
			else{
				Actions.reply(event, "No stages found", null);
			}
		}, () -> Actions.reply(event, "Error while getting data from Overwatch", null));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Info match";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("m", "match");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get the info of a match";
	}
}
