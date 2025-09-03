package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.log.LogContext;
import fr.rakambda.rsndiscord.spring.interaction.modal.ModalRunner;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ModalListener extends ListenerAdapter{
	private final ModalRunner modalRunner;
	
	@Autowired
	public ModalListener(ModalRunner modalRunner){
		this.modalRunner = modalRunner;
	}
	
	@Override
	public void onModalInteraction(@NonNull ModalInteractionEvent event){
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received modal {} from {} with args {}", event.getModalId(), event.getUser(), getArgsForLogs(event.getValues()));
			modalRunner.execute(event);
		}
	}
	
	@NonNull
	private String getArgsForLogs(@NonNull List<ModalMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getCustomId(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
}
