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
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match.Status;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class OverwatchGetWeekMatchesCommand extends BasicCommand{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private static final BiConsumer<GuildMessageReactionAddEvent, WeekData> onWeekData = (event, week) -> {
		final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, week.getName(), null);
		week.getEvents().stream().flatMap(weekEvent -> weekEvent.getMatches().stream()).filter(Objects::nonNull).forEach(m -> {
			final var message = m.getScores().stream().map(Object::toString).collect(Collectors.joining(" - ")) + (m.isLive() ? " (In progress)" : (m.getStatus() == Status.PENDING ? ("On the " + m.getStartDate().format(FORMATTER)) : ""));
			builder.addField(m.getVsCompetitorsNames(), message, false);
		});
		builder.setFooter("ID: " + week.getWeekNumber());
		Actions.reply(event, "", builder.build());
	};
	
	public OverwatchGetWeekMatchesCommand(final Command parent){
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
			final var nextStage = OverwatchUtils.getNextStage(weeksData);
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
		return "Current week matches";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("w", "week");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get the matches of a week";
	}
}
