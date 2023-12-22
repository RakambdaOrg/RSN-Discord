package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableMessageContextMenuGuild extends IExecutableMessageContextMenu {
	@NotNull
	CompletableFuture<?> executeGuild(@NotNull MessageContextInteractionEvent event, @NotNull Guild guild, @NotNull Member member) throws BotException;
}
