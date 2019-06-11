package fr.mrcraftcod.gunterdiscord.listeners.reply;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyMessageListener.class);
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static final List<WaitingUserReply> replies = new ArrayList<>();
	
	public static void handleReply(final WaitingUserReply reply){
		replies.add(reply);
	}
	
	public static void stopAll(){
		executor.shutdown();
	}
	
	public static ScheduledExecutorService getExecutor(){
		return executor;
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			replies.removeIf(reply -> reply.isHandled() || (Objects.equals(reply.getUser(), event.getAuthor()) && Objects.equals(reply.getChannel(), event.getChannel()) && reply.execute(event, Arrays.stream(event.getMessage().getContentRaw().split(" ")).collect(Collectors.toCollection(LinkedList::new)))));
		}
		catch(final Exception e){
			LOGGER.error("Failed to handle user reply", e);
		}
	}
}
