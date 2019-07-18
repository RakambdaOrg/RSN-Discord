package fr.mrcraftcod.gunterdiscord.listeners.reply;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public interface WaitingUserReply{
	boolean execute(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args);
	
	boolean execute(@Nonnull GuildMessageReactionAddEvent event);
	
	boolean onExpire();
	
	boolean handleEvent(GuildMessageReceivedEvent event);
	
	boolean handleEvent(GuildMessageReactionAddEvent event);
	
	long getEmoteMessageId();
	
	@Nonnull
	TextChannel getWaitChannel();
	
	@Nonnull
	User getUser();
	
	boolean isHandled();
}
