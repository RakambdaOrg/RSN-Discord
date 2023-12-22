package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableMessageContextMenuUser extends IExecutableSlashCommand{
	@NotNull
	CompletableFuture<?> executeUser(@NotNull MessageContextInteractionEvent event) throws BotException;
}
