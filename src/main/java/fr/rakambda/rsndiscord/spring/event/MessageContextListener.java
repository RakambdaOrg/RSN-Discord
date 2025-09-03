package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.interaction.context.message.MessageContextMenuRunner;
import fr.rakambda.rsndiscord.spring.log.LogContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MessageContextListener extends ListenerAdapter{
	private final MessageContextMenuRunner messageContextMenuRunner;
	
	@Autowired
	public MessageContextListener(MessageContextMenuRunner messageContextMenuRunner){
        this.messageContextMenuRunner = messageContextMenuRunner;
    }
	
	@Override
	public void onMessageContextInteraction(@NonNull MessageContextInteractionEvent event){
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received message-context {} from {} with args {}", event.getName(), event.getUser(), getArgsForLogs(event.getOptions()));
			messageContextMenuRunner.execute(event);
		}
	}
	
	@NonNull
	private String getArgsForLogs(@NonNull List<OptionMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getName(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
}
