package fr.raksrinana.rsndiscord.button;

import fr.raksrinana.rsndiscord.button.api.IButtonHandler;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@EventListener
@Log4j2
public class ButtonListener extends ListenerAdapter{
	@Override
	public void onButtonClick(@NotNull ButtonClickEvent event){
		super.onButtonClick(event);
		
		try(var context = LogContext.with(event.getGuild()).with(event.getUser())){
			if(event.isFromGuild()){
				var componentId = event.getComponentId();
				log.info("Received button interaction {} from {}", componentId, event.getUser());
				
				ButtonService.getHandler(componentId).ifPresentOrElse(
						handler -> event.deferEdit().submit().thenAccept(empty -> handleClick(event, handler)),
						() -> JDAWrappers.reply(event, "Didn't find the interaction id " + componentId));
			}
		}
	}
	
	private void handleClick(@NotNull ButtonClickEvent event, @NotNull IButtonHandler handler){
		try{
			handler.handle(event).exceptionally(e -> {
				log.error("Failed during button execution {}", handler, e);
				JDAWrappers.reply(event, "Error executing button (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
				return null;
			});
		}
		catch(Exception e){
			log.error("Failed to execute button {}", handler, e);
			JDAWrappers.reply(event, "Error executing button (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
		}
	}
}
