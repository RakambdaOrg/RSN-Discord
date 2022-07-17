package fr.raksrinana.rsndiscord.reply;

import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class BasicWaitingUserReply implements IWaitingUserReply{
	@Getter
	private final List<Message> infoMessages;
	@Getter
	private final GuildMessageChannelUnion waitChannel;
	@Getter
	private final User waitUser;
	private final Object lock;
	@Getter
	private boolean handled;
	
	protected BasicWaitingUserReply(@NotNull User author, @NotNull GuildMessageChannelUnion waitChannel, int delay, @NotNull TimeUnit unit, @NotNull Message... infoMessages){
		lock = new Object();
		this.waitChannel = waitChannel;
		waitUser = author;
		handled = false;
		this.infoMessages = new ArrayList<>();
		this.infoMessages.addAll(Arrays.asList(infoMessages));
		UserReplyEventListener.getExecutor().schedule(() -> {
			synchronized(lock){
				if(!isHandled()){
					handled = onExpire();
				}
			}
		}, delay, unit);
	}
	
	@Override
	public void close(){
	}
	
	@Override
	public boolean execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args){
		synchronized(lock){
			if(!isHandled()){
				handled = onExecute(event, args);
				if(isHandled()){
					infoMessages.forEach(message -> JDAWrappers.delete(message).submit());
				}
			}
		}
		return isHandled();
	}
	
	@Override
	public boolean execute(@NotNull MessageReactionAddEvent event){
		synchronized(lock){
			if(!isHandled()){
				handled = onExecute(event);
				if(isHandled()){
					infoMessages.forEach(message -> JDAWrappers.delete(message).submit());
				}
			}
		}
		return isHandled();
	}
	
	protected abstract boolean onExecute(@NotNull MessageReactionAddEvent event);
	
	@Override
	public boolean onExpire(){
		JDAWrappers.message(getWaitChannel(), translate(getWaitChannel().getGuild(), "listeners.reply.expire", getUser().getAsMention())).submit();
		infoMessages.forEach(message -> JDAWrappers.delete(message).submit());
		return true;
	}
	
	@Override
	public boolean handleEvent(@NotNull MessageReceivedEvent event){
		return Objects.equals(getUser(), event.getAuthor()) && Objects.equals(getWaitChannel(), event.getChannel());
	}
	
	@Override
	public boolean handleEvent(@NotNull MessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException{
		return event.retrieveUser().submit()
				.thenApply(user -> Objects.equals(getUser(), event.getUser())
						&& Objects.equals(getWaitChannel(), event.getChannel())
						&& Objects.equals(getEmoteMessageId(), event.getMessageIdLong()))
				.get(30, SECONDS);
	}
	
	@Override
	public long getEmoteMessageId(){
		return getInfoMessages().stream().map(Message::getIdLong).findFirst().orElse(-1L);
	}
	
	@NotNull
	@Override
	public User getUser(){
		return waitUser;
	}
	
	protected abstract boolean onExecute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args);
}
