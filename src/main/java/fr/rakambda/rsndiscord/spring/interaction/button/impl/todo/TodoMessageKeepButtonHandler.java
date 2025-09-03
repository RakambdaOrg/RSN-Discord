package fr.rakambda.rsndiscord.spring.interaction.button.impl.todo;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.entity.ArchiveEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ArchiveRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@Slf4j
@Component
public class TodoMessageKeepButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "todo-message-keep";
	
	private final ArchiveRepository archiveRepository;
	
	@Autowired
	public TodoMessageKeepButtonHandler(ArchiveRepository archiveRepository){
		this.archiveRepository = archiveRepository;
	}
	
	@Override
	@NonNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NonNull
	@Override
	public CompletableFuture<?> executeGuild(@NonNull ButtonInteraction event, @NonNull Guild guild, @NonNull Member member) throws InvalidChannelTypeException{
		if(event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			throw new InvalidChannelTypeException(event.getChannelType());
		}
		
		var message = event.getMessage();
		var threadChannel = event.getChannel().asThreadChannel();
		
		return event.deferEdit().submit()
				.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenCompose(v -> getThreadEdit(threadChannel));
	}
	
	@NonNull
	private CompletionStage<Void> getThreadEdit(@NonNull ThreadChannel thread){
		var edit = JDAWrappers.editThread(thread).setArchived(true);
		
		var parentChannel = thread.getParentChannel();
		if(parentChannel.getType() == ChannelType.FORUM){
			archiveRepository.findById(parentChannel.getIdLong())
					.map(ArchiveEntity::getTagId)
					.map(ForumTagSnowflake::fromId)
					.ifPresent(edit::addTag);
		}
		
		return edit.submit();
	}
	
	@NonNull
	public static Supplier<Button> builder(){
		return () -> Button.success(COMPONENT_ID, "Archive").withEmoji(Emoji.fromUnicode("U+1F4E6"));
	}
}
