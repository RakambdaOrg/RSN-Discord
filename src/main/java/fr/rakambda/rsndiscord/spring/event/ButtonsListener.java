package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.log.LogContext;
import fr.rakambda.rsndiscord.spring.interaction.button.ButtonRunner;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ButtonsListener extends ListenerAdapter{
	private final ButtonRunner buttonRunner;
	
	@Autowired
	public ButtonsListener(ButtonRunner buttonRunner){
		this.buttonRunner = buttonRunner;
	}
	
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received button {} from {}", event.getComponentId(), event.getUser());
			buttonRunner.execute(event);
		}
	}
}
