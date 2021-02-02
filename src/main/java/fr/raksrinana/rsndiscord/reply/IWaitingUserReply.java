package fr.raksrinana.rsndiscord.reply;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.io.Closeable;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IWaitingUserReply extends Closeable{
	boolean execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args);
	
	boolean execute(@NotNull GuildMessageReactionAddEvent event);
	
	boolean onExpire();
	
	boolean handleEvent(@NotNull GuildMessageReceivedEvent event);
	
	boolean handleEvent(@NotNull GuildMessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException;
	
	long getEmoteMessageId();
	
	@NotNull User getUser();
	
	@NotNull TextChannel getWaitChannel();
	
	boolean isHandled();
}
