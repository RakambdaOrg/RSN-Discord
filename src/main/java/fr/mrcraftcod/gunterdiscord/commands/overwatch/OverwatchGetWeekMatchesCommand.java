package fr.mrcraftcod.gunterdiscord.commands.overwatch;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.OverwatchUtils;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.OverwatchStage;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OverwatchGetWeekMatchesCommand extends BasicCommand{
	public OverwatchGetWeekMatchesCommand(Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		OverwatchUtils.getLastResponse().ifPresentOrElse(overwatchResponse -> {
			if(overwatchResponse.getData().getStages().size() > 0){
				final var counter = new AtomicInteger('a');
				final var options = new HashMap<BasicEmotes, OverwatchStage>();
				final var currentStage = overwatchResponse.getData().getCurrentStage();
				final var nextStage = overwatchResponse.getData().getNextStage();
				final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Available stages");
				overwatchResponse.getData().getStages().forEach(s -> {
					final var emote = BasicEmotes.getEmote("" + ((char) counter.getAndIncrement()));
					options.put(emote, s);
					builder.addField(emote.getValue() + ": " + s.getName(), currentStage.map(s::equals).orElse(false) ? "Current" : (nextStage.map(s::equals).orElse(false) ? "Next" : ""), false);
				});
				Actions.reply(event, message -> {
					options.keySet().stream().sorted().forEachOrdered(e -> message.addReaction(e.getValue()).queue());
					ReplyMessageListener.handleReply(new OverwatchSelectStageWaitUserReply(options, event, message));
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
