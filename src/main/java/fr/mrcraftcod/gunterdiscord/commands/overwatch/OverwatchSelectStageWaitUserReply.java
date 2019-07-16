package fr.mrcraftcod.gunterdiscord.commands.overwatch;

import fr.mrcraftcod.gunterdiscord.listeners.reply.BasicWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.OverwatchStage;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week.OverwatchWeek;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class OverwatchSelectStageWaitUserReply extends BasicWaitingUserReply{
	private final Map<BasicEmotes, OverwatchStage> options;
	
	public OverwatchSelectStageWaitUserReply(Map<BasicEmotes, OverwatchStage> options, GuildMessageReceivedEvent event, Message infoMessage){
		super(event, event.getAuthor(), infoMessage);
		this.options = options;
	}
	
	@Override
	protected boolean onExecute(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
		return false;
	}
	
	@Override
	public boolean onExecute(@Nonnull GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(Objects.nonNull(replyEmote)){
				if(this.options.containsKey(replyEmote)){
					final var stage = options.get(replyEmote);
					if(stage.getWeeks().size() > 0){
						final var counter = new AtomicInteger('a');
						final var options = new HashMap<BasicEmotes, OverwatchWeek>();
						final var currentWeek = stage.getCurrentWeek();
						final var nextWeek = stage.getNextWeek();
						final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, "Available weeks");
						stage.getWeeks().forEach(w -> {
							final var emote = BasicEmotes.getEmote("" + ((char) counter.getAndIncrement()));
							options.put(emote, w);
							builder.addField(emote.getValue() + ": " + w.getName(), currentWeek.map(w::equals).orElse(false) ? "Current" : (nextWeek.map(w::equals).orElse(false) ? "Next" : ""), false);
						});
						Actions.reply(event, message -> {
							options.keySet().stream().sorted().forEachOrdered(e -> message.addReaction(e.getValue()).queue());
							ReplyMessageListener.handleReply(new OverwatchSelectWeekWaitUserReply(options, event, message));
						}, builder.build());
					}
					else{
						Actions.reply(event, "No weeks found");
					}
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return this.getInfoMessages().stream().map(Message::getIdLong).findAny().orElse(-1L);
	}
}
