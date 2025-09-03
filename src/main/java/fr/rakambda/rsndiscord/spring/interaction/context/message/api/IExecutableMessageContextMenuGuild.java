package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableMessageContextMenuGuild extends IExecutableMessageContextMenu {
	@NonNull
	CompletableFuture<?> executeGuild(@NonNull MessageContextInteractionEvent event, @NonNull Guild guild, @NonNull Member member) throws BotException;
}
