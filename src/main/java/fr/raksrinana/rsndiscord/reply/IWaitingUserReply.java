package fr.raksrinana.rsndiscord.reply;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.io.Closeable;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IWaitingUserReply extends Closeable{
	boolean execute(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args);
	
	boolean execute(@NonNull GuildMessageReactionAddEvent event);
	
	boolean onExpire();
	
	boolean handleEvent(GuildMessageReceivedEvent event);
	
	boolean handleEvent(GuildMessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException;
	
	long getEmoteMessageId();
	
	@NonNull User getUser();
	
	@NonNull TextChannel getWaitChannel();
	
	boolean isHandled();
}
