package fr.raksrinana.rsndiscord.commands.overwatch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.reply.EmoteWaitingUserReply;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.OverwatchUtils;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.WeekData;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match.Match;
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
	private static final BiConsumer<GuildMessageReactionAddEvent, Match> onMatch = (event, match) -> {
		final var embed = Utilities.buildEmbed(event.getUser(), Color.GREEN, match.getVsCompetitorsNames(), null);
		match.fillEmbed(embed);
		Actions.reply(event, "", embed.build());
	};
	private static final BiConsumer<GuildMessageReactionAddEvent, WeekData> onWeekData = (event, weekData) -> {
		if(weekData.getMatchCount() > 0){
			if(weekData.getMatchCount() == 1){
				onMatch.accept(event, weekData.getMatches().get(0));
			}
			else{
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, Match>();
				final var currentWeek = weekData.getCurrentMatch();
				final var nextWeek = weekData.getNextMatch();
				final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available matches", null);
				weekData.getMatches().forEach(match -> {
					final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
					options.put(emote, match);
					builder.addField(emote.getValue() + ": " + match.getVsCompetitorsNames(), currentWeek.map(match::equals).orElse(false) ? "Current" : (nextWeek.map(match::equals).orElse(false) ? "Next" : ""), false);
				});
				builder.setFooter("ID: " + weekData.getWeekNumber());
				Actions.reply(event, "", builder.build()).thenAccept(message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, onMatch));
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
		final var weeksData = OverwatchUtils.getWeeksData();
		if(!weeksData.isEmpty()){
			final var counter = new AtomicInteger('a');
			final var options = new HashMap<BasicEmotes, WeekData>();
			final var currentStage = OverwatchUtils.getCurrentStage(weeksData);
			final var nextStage = OverwatchUtils.getCurrentStage(weeksData);
			final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Available weeks", null);
			weeksData.forEach(s -> {
				final var emote = BasicEmotes.getEmote(String.valueOf((char) counter.getAndIncrement()));
				options.put(emote, s);
				builder.addField(emote.getValue() + ": " + s.getName(), currentStage.map(s::equals).orElse(false) ? "Current" : (nextStage.map(s::equals).orElse(false) ? "Next" : ""), false);
			});
			Actions.reply(event, "", builder.build()).thenAccept(message -> {
				options.keySet().stream().sorted().forEachOrdered(e -> Actions.addReaction(message, e.getValue()));
				ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getAuthor(), message, onWeekData));
			});
		}
		else{
			Actions.reply(event, "No weeks found", null);
		}
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
