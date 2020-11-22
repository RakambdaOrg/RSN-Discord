package fr.raksrinana.rsndiscord.reply;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class BasicWaitingUserReply implements IWaitingUserReply{
	private static final int DEFAULT_DELAY = 30;
	@Getter
	private final List<Message> infoMessages;
	@Getter
	private final TextChannel waitChannel;
	@Getter
	private final User waitUser;
	@Getter
	private final long originalMessageId;
	private final Object lock;
	@Getter
	private boolean handled;
	
	protected BasicWaitingUserReply(@NonNull final GenericGuildMessageEvent event, @NonNull final User author, @NonNull final TextChannel waitChannel, final int delay, @NonNull final TimeUnit unit, final Message... infoMessages){
		this.lock = new Object();
		this.waitChannel = waitChannel;
		this.waitUser = author;
		this.originalMessageId = event.getMessageIdLong();
		this.handled = false;
		this.infoMessages = new ArrayList<>();
		this.infoMessages.addAll(Arrays.asList(infoMessages));
		UserReplyEventListener.getExecutor().schedule(() -> {
			synchronized(this.lock){
				if(!this.isHandled()){
					this.handled = this.onExpire();
				}
			}
		}, delay, unit);
	}
	
	@Override
	public void close() throws IOException{
	}
	
	@Override
	public boolean execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		synchronized(this.lock){
			if(!this.isHandled()){
				this.handled = this.onExecute(event, args);
				if(this.isHandled()){
					this.infoMessages.forEach(message -> message.delete().submit());
				}
			}
		}
		return this.isHandled();
	}
	
	@Override
	public boolean execute(@NonNull final GuildMessageReactionAddEvent event){
		synchronized(this.lock){
			if(!this.isHandled()){
				this.handled = this.onExecute(event);
				if(this.isHandled()){
					this.infoMessages.forEach(message -> message.delete().submit());
				}
			}
		}
		return this.isHandled();
	}
	
	protected abstract boolean onExecute(@NonNull final GuildMessageReactionAddEvent event);
	
	@Override
	public boolean onExpire(){
		this.getWaitChannel().sendMessage(translate(getWaitChannel().getGuild(), "listeners.reply.expire", getUser().getAsMention())).submit();
		this.infoMessages.forEach(message -> message.delete().submit());
		return true;
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReceivedEvent event){
		return Objects.equals(getUser(), event.getAuthor()) && Objects.equals(getWaitChannel(), event.getChannel());
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException{
		return event.retrieveUser().submit()
				.thenApply(user -> Objects.equals(getUser(), event.getUser())
						&& Objects.equals(getWaitChannel(), event.getChannel())
						&& Objects.equals(getEmoteMessageId(), event.getMessageIdLong()))
				.get(30, SECONDS);
	}
	
	@Override
	public long getEmoteMessageId(){
		return this.getInfoMessages().stream().map(Message::getIdLong).findFirst().orElse(-1L);
	}
	
	@NonNull
	@Override
	public User getUser(){
		return this.waitUser;
	}
	
	protected abstract boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args);
}
