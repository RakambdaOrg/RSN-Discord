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
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchScore;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week.OverwatchWeek;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class OverwatchGetWeekMatchesCommand extends BasicCommand{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static final BiConsumer<GuildMessageReactionAddEvent, OverwatchWeek> onWeek = (event, week) -> {
		final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, week.getName());
		week.getMatches().forEach(m -> {
			final var message = (m.hasEnded() || m.inProgress()) ? (m.getScores().stream().map(OverwatchScore::getValue).map(Object::toString).collect(Collectors.joining(" - ")) + (m.inProgress() ? " (In progress)" : "")) : ("On the " + m.getStartDate().atZone(ZoneId.of("Europe/Paris")).format(FORMATTER) + " (Europe/Paris)");
			builder.addField(m.getCompetitors().stream().map(c -> Objects.isNull(c) ? "TBD" : c.getName()).collect(Collectors.joining(" vs ")), message, false);
		});
		builder.setFooter("ID: " + week.getId());
		Actions.reply(event, builder.build());
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
					ReplyMessageListener.handleReply(new EmoteWaitingUserReply<>(options, event, event.getUser(), message, OverwatchGetWeekMatchesCommand.onWeek));
				}, builder.build());
			}
		}
		else{
			Actions.reply(event, "No weeks found");
		}
	};
	
	public OverwatchGetWeekMatchesCommand(final Command parent){
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
		return "Current week matches";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("w", "week");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Get the matches of a week";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
