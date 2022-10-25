package fr.raksrinana.rsndiscord.interaction.modal.api;

import fr.raksrinana.rsndiscord.interaction.modal.ModalResult;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IModalHandler{
	@NotNull
	String getComponentId();
	
	@NotNull
	Modal asComponent();
	
	@NotNull
	default CompletableFuture<ModalResult> handleGuild(@NotNull ModalInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		return CompletableFuture.completedFuture(ModalResult.NOT_IMPLEMENTED);
	}
	
	@NotNull
	default CompletableFuture<ModalResult> handleUser(@NotNull ModalInteractionEvent event){
		return CompletableFuture.completedFuture(ModalResult.NOT_IMPLEMENTED);
	}
	
	default boolean deferReply(){
		return true;
	}
}
