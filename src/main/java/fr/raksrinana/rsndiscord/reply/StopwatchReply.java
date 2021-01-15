package fr.raksrinana.rsndiscord.reply;

import fr.raksrinana.rsndiscord.command.impl.StopwatchCommand;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class StopwatchReply extends BasicWaitingUserReply{
	private final ScheduledExecutorService executor;
	private boolean counting = true;
	private ZonedDateTime lastStart = ZonedDateTime.now();
	private Duration totalTime = ZERO;
	
	public StopwatchReply(final GuildMessageReceivedEvent event, final Message message){
		super(event, event.getAuthor(), event.getChannel(), 1, DAYS, message);
		this.executor = Executors.newSingleThreadScheduledExecutor();
		this.executor.scheduleAtFixedRate(this::updateTimer, 5, 10, SECONDS);
	}
	
	private void updateTimer(){
		var newTotalTime = totalTime.plus(counting ? between(lastStart, ZonedDateTime.now()) : ZERO);
		if(!Objects.equals(newTotalTime, totalTime)){
			var embed = StopwatchCommand.buildEmbed(getWaitChannel().getGuild(), getWaitUser(), durationToString(newTotalTime));
			getInfoMessages().stream().findFirst().ifPresent(m -> m.editMessage(embed).submit());
		}
	}
	
	@Override
	public void close() throws IOException{
		super.close();
		this.executor.shutdownNow();
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(nonNull(replyEmote)){
				if(replyEmote == S){
					stop();
					return true;
				}
				if(this.counting){
					if(replyEmote == P){
						this.counting = false;
						this.totalTime = this.totalTime.plus(between(this.lastStart, ZonedDateTime.now()));
					}
				}
				else{
					if(replyEmote == R){
						this.counting = true;
						this.lastStart = ZonedDateTime.now();
					}
				}
				getInfoMessages().stream().findFirst().ifPresent(message -> {
					message.clearReactions().queue();
					if(counting){
						message.addReaction(P.getValue()).submit();
					}
					else{
						message.addReaction(R.getValue()).submit();
					}
					message.addReaction(S.getValue()).submit();
				});
			}
		}
		return false;
	}
	
	@Override
	public boolean onExpire(){
		stop();
		return true;
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReactionAddEvent event){
		return Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		return false;
	}
	
	private void stop(){
		this.counting = false;
		this.totalTime = this.totalTime.plus(between(this.lastStart, ZonedDateTime.now()));
		this.executor.shutdown();
		
		var channel = this.getWaitChannel();
		channel.sendMessage(translate(channel.getGuild(), "stopwatch.total-time", durationToString(this.totalTime))).submit();
	}
}
