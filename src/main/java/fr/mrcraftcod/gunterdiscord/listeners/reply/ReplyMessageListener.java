package fr.mrcraftcod.gunterdiscord.listeners.reply;

import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class ReplyMessageListener extends ListenerAdapter{
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static final List<WaitingUserReply> replies = new ArrayList<>();
	
	public static void handleReply(final WaitingUserReply reply){
		replies.add(reply);
	}
	
	public static void stopAll(){
		executor.shutdown();
		replies.forEach(r -> {
			try{
				r.close();
			}
			catch(IOException e){
				Log.getLogger(null).error("Failed to close reply handler", e);
			}
		});
	}
	
	@Override
	public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			replies.removeIf(reply -> reply.isHandled() || (reply.handleEvent(event) && reply.execute(event, Arrays.stream(event.getMessage().getContentRaw().split(" ")).collect(Collectors.toCollection(LinkedList::new)))));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Failed to handle user reply", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd(@Nonnull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			replies.removeIf(reply -> reply.isHandled() || (reply.handleEvent(event) && reply.execute(event)));
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Failed to handle user reply", e);
		}
	}
	
	@Nonnull
	static ScheduledExecutorService getExecutor(){
		return executor;
	}
}
