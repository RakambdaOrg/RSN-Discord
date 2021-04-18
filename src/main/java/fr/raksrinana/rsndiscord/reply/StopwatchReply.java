package fr.raksrinana.rsndiscord.reply;

import fr.raksrinana.rsndiscord.command.impl.StopwatchCommand;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
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
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class StopwatchReply extends BasicWaitingUserReply{
	private final ScheduledExecutorService executor;
	private boolean counting = true;
	private ZonedDateTime lastStart = ZonedDateTime.now();
	private Duration totalTime = ZERO;
	
	public StopwatchReply(@NotNull GuildMessageReceivedEvent event, @NotNull Message message){
		super(event, event.getAuthor(), event.getChannel(), 1, DAYS, message);
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(this::updateTimer, 5, 10, SECONDS);
	}
	
	private void updateTimer(){
		var newTotalTime = totalTime.plus(counting ? between(lastStart, ZonedDateTime.now()) : ZERO);
		if(!Objects.equals(newTotalTime, totalTime)){
			var embed = StopwatchCommand.buildEmbed(getWaitChannel().getGuild(), getWaitUser(), durationToString(newTotalTime));
			getInfoMessages().stream().findFirst().ifPresent(m -> JDAWrappers.edit(m, embed).submit());
		}
	}
	
	@Override
	public void close() throws IOException{
		super.close();
		executor.shutdownNow();
	}
	
	@Override
	protected boolean onExecute(@NotNull GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(replyEmote == S){
				stop();
				return true;
			}
			if(counting){
				if(replyEmote == P){
					counting = false;
					totalTime = totalTime.plus(between(lastStart, ZonedDateTime.now()));
				}
			}
			else{
				if(replyEmote == R){
					counting = true;
					lastStart = ZonedDateTime.now();
				}
			}
			getInfoMessages().stream().findFirst().ifPresent(message -> {
				JDAWrappers.clearReactions(message).submit();
				if(counting){
					JDAWrappers.addReaction(message, P).submit();
				}
				else{
					JDAWrappers.addReaction(message, R).submit();
				}
				JDAWrappers.addReaction(message, S).submit();
			});
		}
		return false;
	}
	
	@Override
	public boolean onExpire(){
		stop();
		return true;
	}
	
	@Override
	public boolean handleEvent(@NotNull GuildMessageReactionAddEvent event){
		return Objects.equals(getWaitChannel(), event.getChannel()) && Objects.equals(getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	protected boolean onExecute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		return false;
	}
	
	private void stop(){
		counting = false;
		totalTime = totalTime.plus(between(lastStart, ZonedDateTime.now()));
		executor.shutdown();
		
		var channel = getWaitChannel();
		JDAWrappers.message(channel, translate(channel.getGuild(), "stopwatch.total-time", durationToString(totalTime))).submit();
	}
}
