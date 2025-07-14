package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.interaction.button.impl.todo.TodoMessageDeleteButtonHandler;
import fr.rakambda.rsndiscord.spring.interaction.button.impl.todo.TodoMessageKeepButtonHandler;
import fr.rakambda.rsndiscord.spring.interaction.button.impl.todo.TodoMessageKeepWithoutThreadButtonHandler;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.log.LogContext;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
@Slf4j
public class AutoTodoEventListener extends ListenerAdapter{
	private static final MessageTopLevelComponent BUTTONS_NORMAL = ActionRow.of(
			TodoMessageDeleteButtonHandler.builder().get(),
			TodoMessageKeepButtonHandler.builder().get(),
			TodoMessageKeepWithoutThreadButtonHandler.builder().get()
	);
	private static final MessageTopLevelComponent BUTTONS_FORUM = ActionRow.of(
			TodoMessageDeleteButtonHandler.builder().get(),
			TodoMessageKeepButtonHandler.builder().get()
	);
	
	private final ChannelRepository channelRepository;
	
	@Autowired
	public AutoTodoEventListener(ChannelRepository channelRepository){
		this.channelRepository = channelRepository;
	}
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		if(!event.isFromGuild() || event.getChannelType() != ChannelType.TEXT){
			return;
		}
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getAuthor())){
			
			if(isChannelNotRegistered(event.getChannel().asGuildMessageChannel())){
				log.debug("Channel not registered for auto-todo {}", event.getChannel());
				return;
			}
			
			log.info("Handling auto-todo in {}", event.getChannel());
			
			var message = event.getMessage();
			switch(message.getType()){
				case CHANNEL_PINNED_ADD, THREAD_CREATED -> JDAWrappers.delete(message).submit();
				case DEFAULT, INLINE_REPLY, SLASH_COMMAND, CONTEXT_COMMAND -> JDAWrappers.delay(5)
						.thenCompose(empty -> JDAWrappers.createThread(message, "reply-" + event.getMessageId()).submit())
						.thenCompose(thread -> JDAWrappers.message(thread, BUTTONS_NORMAL).submit());
			}
		}
	}
	
	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event){
		if(!event.isFromGuild() || event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			return;
		}
		
		try(var ignored = LogContext.with(event.getGuild())){
			var threadChannel = event.getChannel().asThreadChannel();
			var parentChannel = threadChannel.getParentChannel();
			if(parentChannel.getType() != ChannelType.FORUM){
				return;
			}
			
			if(isChannelNotRegistered(parentChannel)){
				log.debug("Channel not registered for auto-todo {}", event.getChannel());
				return;
			}
			
			log.info("Handling auto-todo in {}", event.getChannel());
			
			JDAWrappers.delay(5)
					.thenCompose(empty -> JDAWrappers.message(threadChannel, BUTTONS_FORUM).submit());
		}
	}
	
	public boolean isChannelNotRegistered(@NotNull GuildChannel channel){
		return channelRepository.findAllByGuildIdAndType(channel.getGuild().getIdLong(), fr.rakambda.rsndiscord.spring.storage.entity.ChannelType.AUTO_TODO).stream()
				.noneMatch(channelEntity -> Objects.equals(channelEntity.getChannelId(), channel.getIdLong()));
	}
}
