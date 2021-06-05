package fr.raksrinana.rsndiscord.reply;

import fr.raksrinana.rsndiscord.event.EventListener;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.reportException;

@EventListener
@Log4j2
public class UserReplyEventListener extends ListenerAdapter{
	@Getter
	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static final List<IWaitingUserReply> replies = new ArrayList<>();
	
	public static void handleReply(@NotNull IWaitingUserReply reply){
		replies.add(reply);
	}
	
	public static void stopAll(){
		executor.shutdown();
		replies.forEach(reply -> {
			try{
				reply.close();
			}
			catch(IOException e){
				log.error("Failed to close reply handler", e);
			}
		});
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			replies.removeIf(reply -> reply.isHandled()
					|| (reply.handleEvent(event) && reply.execute(event, Arrays.stream(event.getMessage().getContentRaw().split(" "))
					.collect(Collectors.toCollection(LinkedList::new)))));
		}
		catch(Exception e){
			log.error("Failed to handle user reply", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			replies.removeIf(reply -> {
				try{
					return reply.isHandled() || (reply.handleEvent(event) && reply.execute(event));
				}
				catch(InterruptedException | ExecutionException | TimeoutException e){
					reportException("Failed to handle reaction", e);
					log.error("Failed to handle reaction");
				}
				return false;
			});
		}
		catch(Exception e){
			log.error("Failed to handle user reply", e);
		}
	}
}
