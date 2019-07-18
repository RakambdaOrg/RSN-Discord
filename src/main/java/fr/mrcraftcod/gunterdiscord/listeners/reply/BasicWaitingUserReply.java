package fr.mrcraftcod.gunterdiscord.listeners.reply;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BasicWaitingUserReply implements WaitingUserReply{
	private final List<Message> infoMessages;
	private final TextChannel waitChannel;
	private final User waitUser;
	private final long originalMessageId;
	private boolean handled;
	private final Object lock;
	
	protected BasicWaitingUserReply(@Nonnull final GenericGuildMessageEvent event, @Nonnull final User author, final Message... infoMessages){
		this(event, author, event.getChannel(), infoMessages);
	}
	
	protected BasicWaitingUserReply(@Nonnull final GenericGuildMessageEvent event, @Nonnull final User author, @Nonnull final TextChannel waitChannel, final Message... infoMessages){
		this(event, author, waitChannel, 30, TimeUnit.SECONDS, infoMessages);
	}
	
	protected BasicWaitingUserReply(@Nonnull final GenericGuildMessageEvent event, @Nonnull final User author, @Nonnull final TextChannel waitChannel, final int delay, @Nonnull final TimeUnit unit, final Message... infoMessages){
		this.lock = new Object();
		this.waitChannel = waitChannel;
		this.waitUser = author;
		this.originalMessageId = event.getMessageIdLong();
		this.handled = false;
		this.infoMessages = new ArrayList<>();
		this.infoMessages.addAll(Arrays.asList(infoMessages));
		ReplyMessageListener.getExecutor().schedule(() -> {
			synchronized(this.lock){
				if(!isHandled()){
					this.handled = this.onExpire();
				}
			}
		}, delay, unit);
	}
	
	public void addMessage(@Nonnull final Message message){
		this.infoMessages.add(message);
	}
	
	@Override
	public boolean execute(@Nonnull final GuildMessageReactionAddEvent event){
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
	
	protected abstract boolean onExecute(@Nonnull final GuildMessageReactionAddEvent event);
	
	@Nonnull
	@Override
	public TextChannel getWaitChannel(){
		return this.waitChannel;
	}
	
	@Override
	public boolean isHandled(){
		return this.handled;
	}
	
	@Override
	public boolean execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
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
	
	protected List<Message> getInfoMessages(){
		return this.infoMessages;
	}
	
	protected abstract boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args);
	
	protected long getOriginalMessageId(){
		return originalMessageId;
	}
	
	@Override
	public boolean onExpire(){
		Actions.sendMessage(this.getWaitChannel(), "%s you didn't reply in time", this.getUser().getAsMention());
		this.infoMessages.forEach(Actions::deleteMessage);
		return true;
	}
	
	@Nonnull
	@Override
	public User getUser(){
		return this.waitUser;
	}
	
	@Override
	public boolean handleEvent(GuildMessageReceivedEvent event){
		return Objects.equals(this.getUser(), event.getAuthor()) && Objects.equals(this.getWaitChannel(), event.getChannel());
	}
	
	@Override
	public boolean handleEvent(GuildMessageReactionAddEvent event){
		return Objects.equals(this.getUser(), event.getUser()) && Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	public long getEmoteMessageId(){
		return this.getInfoMessages().stream().map(Message::getIdLong).findFirst().orElse(-1L);
	}
}
