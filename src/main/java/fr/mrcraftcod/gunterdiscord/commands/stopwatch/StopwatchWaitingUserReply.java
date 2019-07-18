package fr.mrcraftcod.gunterdiscord.commands.stopwatch;

import fr.mrcraftcod.gunterdiscord.listeners.reply.BasicWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StopwatchWaitingUserReply extends BasicWaitingUserReply{
	private final ScheduledExecutorService executor;
	private boolean counting = true;
	private LocalDateTime lastStart = LocalDateTime.now();
	private Duration totalTime = Duration.ZERO;
	
	public StopwatchWaitingUserReply(GuildMessageReceivedEvent event, Message message){
		super(event, event.getAuthor(), event.getChannel(), 1, TimeUnit.DAYS, message);
		this.executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> {
			final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Stopwatch");
			builder.addField("Time", totalTime.plus(counting ? Duration.between(this.lastStart, LocalDateTime.now()) : Duration.ZERO).toString(), false);
			builder.addField(BasicEmotes.P.getValue(), "Pause", true);
			builder.addField(BasicEmotes.R.getValue(), "Resume", true);
			builder.addField(BasicEmotes.S.getValue(), "Stop", true);
			message.editMessage(builder.build()).queue();
		}, 5, 10, TimeUnit.SECONDS);
	}
	
	@Override
	protected boolean onExecute(@Nonnull GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(Objects.nonNull(replyEmote)){
				if(replyEmote == BasicEmotes.S){
					counting = false;
					totalTime = totalTime.plus(Duration.between(lastStart, LocalDateTime.now()));
					executor.shutdown();
					Actions.sendMessage(this.getWaitChannel(), "Total time: " + this.totalTime);
					return true;
				}
				if(counting){
					if(replyEmote == BasicEmotes.P){
						counting = false;
						totalTime = totalTime.plus(Duration.between(lastStart, LocalDateTime.now()));
						final var message = this.getInfoMessages().stream().findFirst();
						message.ifPresent(m -> {
							m.clearReactions().queue();
							m.addReaction(BasicEmotes.R.getValue()).queue();
							m.addReaction(BasicEmotes.S.getValue()).queue();
						});
					}
				}
				else{
					if(replyEmote == BasicEmotes.R){
						counting = true;
						lastStart = LocalDateTime.now();
						final var message = this.getInfoMessages().stream().findFirst();
						message.ifPresent(m -> {
							m.clearReactions().queue();
							m.addReaction(BasicEmotes.P.getValue()).queue();
							m.addReaction(BasicEmotes.S.getValue()).queue();
						});
					}
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean onExecute(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
		return false;
	}
	
	@Override
	public boolean onExpire(){
		counting = false;
		totalTime = totalTime.plus(Duration.between(lastStart, LocalDateTime.now()));
		executor.shutdown();
		Actions.sendMessage(this.getWaitChannel(), "Total time: " + this.totalTime);
		return true;
	}
	
	@Override
	public boolean handleEvent(GuildMessageReactionAddEvent event){
		return Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
	}
}
