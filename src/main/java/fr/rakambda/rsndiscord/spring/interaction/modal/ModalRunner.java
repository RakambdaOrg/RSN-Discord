package fr.rakambda.rsndiscord.spring.interaction.modal;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInGuildException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInUserException;
import fr.rakambda.rsndiscord.spring.interaction.modal.api.IExecutableModal;
import fr.rakambda.rsndiscord.spring.interaction.modal.api.IExecutableModalGuild;
import fr.rakambda.rsndiscord.spring.interaction.modal.api.IExecutableModalUser;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ModalRunner{
	private final ModalService modalService;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public ModalRunner(ModalService modalService, LocalizationService localizationService, RabbitService rabbitService){
		this.modalService = modalService;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	public void execute(@NonNull ModalInteraction event){
		CompletableFuture.completedFuture(event.getModalId())
				.thenApply(id -> modalService.getExecutableModal(id)
						.orElseThrow(() -> new IllegalStateException("Unknown modal %s".formatted(id))))
				.thenCompose(cmd -> runModal(event, cmd))
				.exceptionally(ex -> handleExecutionError(event, ex));
	}
	
	@NonNull
	private CompletableFuture<?> runModal(@NonNull ModalInteraction event, @NonNull IExecutableModal modal){
		try{
			if(event.isFromGuild()){
				if(!(modal instanceof IExecutableModalGuild modalGuild)){
					return CompletableFuture.failedFuture(new NotAvailableInGuildException());
				}
				return modalGuild.executeGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				if(!(modal instanceof IExecutableModalUser modalUser)){
					return CompletableFuture.failedFuture(new NotAvailableInUserException());
				}
				return modalUser.executeUser(event);
			}
		}
		catch(BotException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@Nullable
	private <T> T handleExecutionError(@NonNull ModalInteraction event, @NonNull Throwable ex){
		log.error("Failed to execute modal {}", event.getModalId(), ex);
		
		var exceptionMessage = ex instanceof BotException bex
				? localizationService.translate(event.getUserLocale(), bex.getFriendlyMessageKey(), bex.getFriendlyMessageArgs())
				: ex.getMessage();
		var message = "Failed to execute modal %s: %s".formatted(event.getModalId(), exceptionMessage);
		
		if(event.isAcknowledged()){
			JDAWrappers.edit(event, message).submitAndDelete(5, rabbitService);
		}
		else{
			JDAWrappers.reply(event, message).ephemeral(true).submit();
		}
		return null;
	}
}
