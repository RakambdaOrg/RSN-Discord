package fr.raksrinana.rsndiscord.interaction.component;

import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@EventListener
@Log4j2
public class InteractionListener extends ListenerAdapter{
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event){
		super.onButtonInteraction(event);
		onInteraction(event, ComponentService::getButtonHandler);
	}
	
	private <T extends Component, E extends ComponentInteraction> void onInteraction(@NotNull E event, @NotNull Function<String, Optional<? extends IComponentHandler<T, E>>> handlerSupplier){
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			var componentId = event.getComponentId();
			log.info("Received interaction {} from {}", componentId, event.getUser());
			
			handlerSupplier.apply(componentId).ifPresentOrElse(
					handler -> {
						if(handler.deferReply()){
							event.deferEdit().submit().thenAccept(empty -> handleInteraction(event, handler));
						}
						else{
							handleInteraction(event, handler);
						}
					},
					() -> JDAWrappers.reply(event, "Didn't find the interaction id " + componentId));
		}
	}
	
	private <E extends ComponentInteraction> void handleInteraction(@NotNull E event, @NotNull IComponentHandler<?, E> handler){
		try{
			CompletableFuture<ComponentResult> result;
			if(event.isFromGuild()){
				result = handler.handleGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				result = handler.handleUser(event);
			}
			
			result.thenAccept(r -> {
						switch(r){
							case HANDLED -> {
							}
							case NOT_IMPLEMENTED -> JDAWrappers.edit(event, "This button cannot be used in this channel").submit();
						}
					})
					.exceptionally(e -> {
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
