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
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class StopwatchWaitingUserReply extends BasicWaitingUserReply{
	private static final Pattern TIME_PATTERN = Pattern.compile("(\\d[HMS])(?!$)");
	private final ScheduledExecutorService executor;
	private boolean counting = true;
	private LocalDateTime lastStart = LocalDateTime.now();
	private Duration totalTime = Duration.ZERO;
	
	public StopwatchWaitingUserReply(final GuildMessageReceivedEvent event, final Message message){
		super(event, event.getAuthor(), event.getChannel(), 1, TimeUnit.DAYS, message);
		this.executor = Executors.newSingleThreadScheduledExecutor();
		this.executor.scheduleAtFixedRate(this::updateTimer, 5, 10, TimeUnit.SECONDS);
	}
	
	private void updateTimer(){
		final var newTotalTime = this.totalTime.plus(this.counting ? Duration.between(this.lastStart, LocalDateTime.now()) : Duration.ZERO);
		if(!Objects.equals(newTotalTime, this.totalTime)){
			final var builder = Utilities.buildEmbed(this.getWaitUser(), Color.GREEN, "Stopwatch");
			builder.addField("Time", Utilities.durationToString(newTotalTime), false);
			builder.addField(BasicEmotes.P.getValue(), "Pause", true);
			builder.addField(BasicEmotes.R.getValue(), "Resume", true);
			builder.addField(BasicEmotes.S.getValue(), "Stop", true);
			this.getInfoMessages().stream().findFirst().ifPresent(m -> m.editMessage(builder.build()).queue());
		}
	}
	
	@Override
	protected boolean onExecute(@Nonnull final GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(Objects.nonNull(replyEmote)){
				if(replyEmote == BasicEmotes.S){
					this.counting = false;
					this.totalTime = this.totalTime.plus(Duration.between(this.lastStart, LocalDateTime.now()));
					this.executor.shutdownNow();
					Actions.sendMessage(this.getWaitChannel(), "Total time: " + this.totalTime);
					return true;
				}
				if(this.counting){
					if(replyEmote == BasicEmotes.P){
						this.counting = false;
						this.totalTime = this.totalTime.plus(Duration.between(this.lastStart, LocalDateTime.now()));
						final var message = this.getInfoMessages().stream().findFirst();
						message.ifPresent(m -> {
							m.clearReactions().queue();
							m.addReaction(BasicEmotes.R.getValue()).queue();
							m.addReaction(BasicEmotes.S.getValue()).queue();
							this.updateTimer();
						});
					}
				}
				else{
					if(replyEmote == BasicEmotes.R){
						this.counting = true;
						this.lastStart = LocalDateTime.now();
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
	protected boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		return false;
	}
	
	@Override
	public boolean onExpire(){
		this.counting = false;
		this.totalTime = this.totalTime.plus(Duration.between(this.lastStart, LocalDateTime.now()));
		this.executor.shutdown();
		Actions.sendMessage(this.getWaitChannel(), "Total time: " + this.totalTime);
		return true;
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReactionAddEvent event){
		return Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	public void close() throws IOException{
		super.close();
		this.executor.shutdownNow();
	}
}
