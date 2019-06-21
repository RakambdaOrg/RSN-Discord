package fr.mrcraftcod.gunterdiscord.listeners.reply;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class BasicWaitingUserReply implements WaitingUserReply{
	private final List<Message> infoMessages;
	private final TextChannel waitChannel;
	private final User waitUser;
	private boolean handled;
	private final Object lock;
	
	protected BasicWaitingUserReply(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final TextChannel waitChannel, final int delay, @Nonnull final TimeUnit unit, final Message... infoMessages){
		this.lock = new Object();
		this.waitChannel = waitChannel;
		this.waitUser = event.getAuthor();
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
	
	protected BasicWaitingUserReply(@Nonnull final GuildMessageReceivedEvent event, final Message... infoMessages){
		this(event, event.getChannel(), infoMessages);
	}
	
	protected BasicWaitingUserReply(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final TextChannel waitChannel, final Message... infoMessages){
		this(event, waitChannel, 30, TimeUnit.SECONDS, infoMessages);
	}
	
	public void addMessage(@Nonnull final Message message){
		this.infoMessages.add(message);
	}
	
	@NotNull
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
	
	protected abstract boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args);
	
	@Override
	public boolean onExpire(){
		Actions.sendMessage(this.getWaitChannel(), "%s you didn't reply in time", this.getUser().getAsMention());
		this.infoMessages.forEach(Actions::deleteMessage);
		return true;
	}
	
	@NotNull
	@Override
	public User getUser(){
		return this.waitUser;
	}
}
