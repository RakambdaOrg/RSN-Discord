package fr.raksrinana.rsndiscord.reply;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.io.Closeable;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IWaitingUserReply extends Closeable{
	boolean execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args);
	
	boolean execute(@NotNull MessageReactionAddEvent event);
	
	boolean onExpire();
	
	boolean handleEvent(@NotNull MessageReceivedEvent event);
	
	boolean handleEvent(@NotNull MessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException;
	
	long getEmoteMessageId();
	
	@NotNull User getUser();
	
	@NotNull GuildMessageChannelUnion getWaitChannel();
	
	boolean isHandled();
}
