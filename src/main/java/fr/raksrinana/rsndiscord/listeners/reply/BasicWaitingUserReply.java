package fr.raksrinana.rsndiscord.listeners.reply;

import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BasicWaitingUserReply implements WaitingUserReply{
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
	
	protected BasicWaitingUserReply(@NonNull final GenericGuildMessageEvent event, @NonNull final User author, final Message... infoMessages){
		this(event, author, event.getChannel(), infoMessages);
	}
	
	private BasicWaitingUserReply(@NonNull final GenericGuildMessageEvent event, @NonNull final User author, @NonNull final TextChannel waitChannel, final Message... infoMessages){
		this(event, author, waitChannel, DEFAULT_DELAY, TimeUnit.SECONDS, infoMessages);
	}
	
	protected BasicWaitingUserReply(@NonNull final GenericGuildMessageEvent event, @NonNull final User author, @NonNull final TextChannel waitChannel, final int delay, @NonNull final TimeUnit unit, final Message... infoMessages){
		this.lock = new Object();
		this.waitChannel = waitChannel;
		this.waitUser = author;
		this.originalMessageId = event.getMessageIdLong();
		this.handled = false;
		this.infoMessages = new ArrayList<>();
		this.infoMessages.addAll(Arrays.asList(infoMessages));
		ReplyMessageListener.getExecutor().schedule(() -> {
			synchronized(this.lock){
				if(!this.isHandled()){
					this.handled = this.onExpire();
				}
			}
		}, delay, unit);
	}
	
	public void addMessage(@NonNull final Message message){
		this.infoMessages.add(message);
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
					this.infoMessages.forEach(Actions::deleteMessage);
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
					this.infoMessages.forEach(Actions::deleteMessage);
				}
			}
		}
		return this.isHandled();
	}
	
	protected abstract boolean onExecute(@NonNull final GuildMessageReactionAddEvent event);
	
	@Override
	public boolean onExpire(){
		Actions.sendMessage(this.getWaitChannel(), MessageFormat.format("{0} you didn't reply in time", this.getUser().getAsMention()), null);
		this.infoMessages.forEach(Actions::deleteMessage);
		return true;
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReceivedEvent event){
		return Objects.equals(this.getUser(), event.getAuthor()) && Objects.equals(this.getWaitChannel(), event.getChannel());
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReactionAddEvent event){
		return Objects.equals(this.getUser(), event.getUser()) && Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
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
