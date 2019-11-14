package fr.raksrinana.rsndiscord.listeners.reply;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.io.Closeable;
import java.util.LinkedList;

interface WaitingUserReply extends Closeable{
	boolean execute(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args);
	
	boolean execute(@NonNull GuildMessageReactionAddEvent event);
	
	boolean onExpire();
	
	boolean handleEvent(GuildMessageReceivedEvent event);
	
	boolean handleEvent(GuildMessageReactionAddEvent event);
	
	long getEmoteMessageId();
	
	@NonNull User getUser();
	
	@NonNull TextChannel getWaitChannel();
	
	boolean isHandled();
}
