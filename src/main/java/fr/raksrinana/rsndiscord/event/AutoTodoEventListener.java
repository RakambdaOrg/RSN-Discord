package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageDeleteButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageKeepButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.TodoMessageKeepWithoutThreadButtonHandler;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
@Getter
@Log4j2
public class AutoTodoEventListener extends ListenerAdapter{
	private static final ItemComponent[] BUTTONS_NORMAL = {
			new TodoMessageDeleteButtonHandler().asComponent(),
			new TodoMessageKeepButtonHandler().asComponent(),
			new TodoMessageKeepWithoutThreadButtonHandler().asComponent()
	};
	private static final ItemComponent[] BUTTONS_FORUM = {
			new TodoMessageDeleteButtonHandler().asComponent(),
			new TodoMessageKeepButtonHandler().asComponent()
	};
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		if(!event.isFromGuild() || event.getChannelType() != ChannelType.TEXT){
			return;
		}
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getAuthor())){
			var guildConfiguration = Settings.get(event.getGuild());
			var message = event.getMessage();
			
			if(containsChannel(guildConfiguration.getReactionsConfiguration().getAutoTodoChannels(), event.getChannel().asGuildMessageChannel())){
				switch(message.getType()){
					case CHANNEL_PINNED_ADD, THREAD_CREATED -> JDAWrappers.delete(message).submit();
					case DEFAULT, INLINE_REPLY, SLASH_COMMAND, CONTEXT_COMMAND -> JDAWrappers
							.createThread(event.getMessage(), "reply-" + event.getMessageId()).submit()
							.thenCompose(thread -> JDAWrappers.message(thread, ActionRow.of(BUTTONS_NORMAL)).submit());
				}
			}
		}
		catch(Exception e){
			log.error("Error handling message", e);
		}
	}
	
	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event){
		if(!event.isFromGuild() || event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			return;
		}
		
		try(var ignored = LogContext.with(event.getGuild())){
			var threadChannel = event.getChannel().asThreadChannel();
			if(threadChannel.getParentChannel().getType() != ChannelType.FORUM){
				return;
			}
			
			JDAWrappers.delay(10)
					.thenCompose(channel -> JDAWrappers.message(threadChannel, ActionRow.of(BUTTONS_FORUM)).submit());
		}
	}
	
	@Override
	public void onThreadHidden(@NotNull ThreadHiddenEvent event){
		try(var ignored = LogContext.with(event.getGuild())){
			var threadChannel = event.getThread();
			var parentChannel = threadChannel.getParentChannel();
			if(threadChannel.isLocked()){
				return;
			}
			
			var guildConfiguration = Settings.get(event.getGuild());
			
			if(containsChannel(guildConfiguration.getReactionsConfiguration().getAutoTodoChannels(), parentChannel)){
				JDAWrappers.message(threadChannel, "Bump").submit();
			}
		}
		catch(Exception e){
			log.error("Error handling message", e);
		}
	}
}
