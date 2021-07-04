package fr.raksrinana.rsndiscord.button;

import fr.raksrinana.rsndiscord.button.api.ISelectionMenuHandler;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@EventListener
@Log4j2
public class SelectionMenuListener extends ListenerAdapter{
	@Override
	public void onSelectionMenu(@NotNull SelectionMenuEvent event){
		super.onSelectionMenu(event);
		
		try(var context = LogContext.with(event.getGuild()).with(event.getUser())){
			if(event.isFromGuild()){
				var componentId = event.getComponentId();
				log.info("Received selection menu interaction {} from {}", componentId, event.getUser());
				
				ComponentService.getSelectionMenuHandler(componentId).ifPresentOrElse(
						handler -> event.deferEdit().submit().thenAccept(empty -> handleSelection(event, handler)),
						() -> JDAWrappers.reply(event, "Didn't find the interaction id " + componentId));
			}
		}
	}
	
	private void handleSelection(@NotNull SelectionMenuEvent event, @NotNull ISelectionMenuHandler handler){
		try{
			handler.handle(event).exceptionally(e -> {
				log.error("Failed during selection menu execution {}", handler, e);
				JDAWrappers.reply(event, "Error executing selection menu (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
				return null;
			});
		}
		catch(Exception e){
			log.error("Failed to execute selection menu {}", handler, e);
			JDAWrappers.reply(event, "Error executing selection menu (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
		}
	}
}
