package fr.raksrinana.rsndiscord.interaction.modal;

import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.interaction.modal.api.IModalHandler;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@EventListener
@Log4j2
public class ModalListener extends ListenerAdapter{
	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event){
		super.onModalInteraction(event);
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received modal {} from {} with args {}", event.getModalId(), event.getUser(), getArgsForLogs(event.getValues()));
			
			ModalService.getModalHandler(event.getModalId()).ifPresentOrElse(
					modal -> performInteraction(event, modal),
					() -> JDAWrappers.reply(event, "Unknown modal {%s".formatted(event.getModalId())).ephemeral(true).submit());
		}
	}
	
	@NotNull
	private String getArgsForLogs(@NotNull List<ModalMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getId(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
	
	private void performInteraction(@NotNull ModalInteractionEvent event, @NotNull IModalHandler modal){
		try{
			if(modal.deferReply()){
				event.deferReply().complete();
			}
			
			CompletableFuture<ModalResult> result;
			if(event.isFromGuild()){
				result = modal.handleGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				result = modal.handleUser(event);
			}
			
			result.thenAccept(r -> {
						switch(r){
							case HANDLED -> {
							}
							case FAILED -> JDAWrappers.reply(event, "Failed to execute modal " + event.getModalId()).ephemeral(true).submit();
							case NOT_IMPLEMENTED -> JDAWrappers.reply(event, "This modal cannot be used in this channel").ephemeral(true).submit();
						}
					})
					.exceptionally(e -> {
						log.error("Failed during modal execution {}", modal, e);
						JDAWrappers.reply(event, "Error executing modal (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
						return null;
					});
		}
		catch(Exception e){
			log.error("Failed to execute modal {}", modal, e);
			JDAWrappers.edit(event, "Error executing modal (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
		}
	}
}
