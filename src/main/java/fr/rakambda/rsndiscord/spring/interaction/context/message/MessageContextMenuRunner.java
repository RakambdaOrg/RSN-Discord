package fr.rakambda.rsndiscord.spring.interaction.context.message;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IExecutableMessageContextMenu;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IExecutableMessageContextMenuGuild;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IExecutableMessageContextMenuUser;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAllowedException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInGuildException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInUserException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MessageContextMenuRunner {
	private final MessageContextMenuService messageContextMenuService;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public MessageContextMenuRunner(MessageContextMenuService messageContextMenuService, LocalizationService localizationService, RabbitService rabbitService){
		this.messageContextMenuService = messageContextMenuService;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	public void execute(@NotNull MessageContextInteractionEvent event){
		CompletableFuture.completedFuture(event.getName())
				.thenApply(name -> messageContextMenuService.getExecutableContextMenu(name)
						.orElseThrow(() -> new IllegalStateException("Unknown message context menu %s".formatted(name))))
				.thenCompose(menu -> verifyAllowed(event, menu))
				.thenCompose(menu -> runMenu(event, menu))
				.exceptionally(ex -> handleExecutionError(event, ex));
	}
	
	@NotNull
	private CompletableFuture<?> runMenu(@NotNull MessageContextInteractionEvent event, @NotNull IExecutableMessageContextMenu menu){
		try{
			if(event.isFromGuild()){
				
				if(!(menu instanceof IExecutableMessageContextMenuGuild menuGuild)){
					return CompletableFuture.failedFuture(new NotAvailableInGuildException());
				}
				return menuGuild.executeGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				if(!(menu instanceof IExecutableMessageContextMenuUser menuUser)){
					return CompletableFuture.failedFuture(new NotAvailableInUserException());
				}
				return menuUser.executeUser(event);
			}
		}
		catch(Exception e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@NotNull
	private CompletableFuture<IExecutableMessageContextMenu> verifyAllowed(@NotNull MessageContextInteractionEvent event, @NotNull IExecutableMessageContextMenu menu){
		if(!menu.getPermission().isAllowed(event.getUser())){
			return CompletableFuture.failedFuture(new NotAllowedException());
		}
		return CompletableFuture.completedFuture(menu);
	}
	
	@Nullable
	private <T> T handleExecutionError(@NotNull MessageContextInteractionEvent event, @NotNull Throwable ex){
		log.error("Failed to execute message context menu {}", event.getName(), ex);
		
		var exceptionMessage = ex instanceof BotException bex
				? localizationService.translate(event.getUserLocale(), bex.getFriendlyMessageKey(), bex.getFriendlyMessageArgs())
				: ex.getMessage();
		var message = "Failed to execute message context menu %s: %s".formatted(event.getName(), exceptionMessage);
		
		if(event.isAcknowledged()){
			JDAWrappers.edit(event, message).submitAndDelete(5, rabbitService);
		}
		else{
			JDAWrappers.reply(event, message).ephemeral(true).submit();
		}
		return null;
	}
}
