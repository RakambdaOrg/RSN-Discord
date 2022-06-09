package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageDeleteButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageKeepButtonHandler;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
@Getter
@Log4j2
public class AutoTodoEventListener extends ListenerAdapter{
	private static final ItemComponent[] buttons = {
			new TodoMessageDeleteButtonHandler().asComponent(),
			new TodoMessageKeepButtonHandler().asComponent()
	};
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		super.onMessageReceived(event);
		if(!event.isFromGuild()){
			return;
		}
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getAuthor())){
			var guildConfiguration = Settings.get(event.getGuild());
			var message = event.getMessage();
			
			if(containsChannel(guildConfiguration.getReactionsConfiguration().getAutoTodoChannels(), event.getChannel())){
				switch(message.getType()){
					case CHANNEL_PINNED_ADD, THREAD_CREATED -> JDAWrappers.delete(message).submit();
					case DEFAULT -> handleTodo(event);
				}
			}
		}
		catch(Exception e){
			log.error("Error handling message", e);
		}
	}
	
	private void handleTodo(@NotNull MessageReceivedEvent event){
		JDAWrappers.createThread(event.getMessage(), "reply-" + event.getMessageId()).submit()
				.thenCompose(thread -> JDAWrappers.editThread(thread)
						.setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.TIME_1_WEEK)
						.submitAndGet())
				.thenCompose(thread -> JDAWrappers.message(thread, "Interact with this messages with the buttons below")
						.setActionRows(ActionRow.of(buttons))
						.submit());
	}
}
