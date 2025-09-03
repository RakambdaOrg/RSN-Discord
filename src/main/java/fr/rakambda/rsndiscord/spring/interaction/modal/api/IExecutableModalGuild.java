package fr.rakambda.rsndiscord.spring.interaction.modal.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableModalGuild extends IExecutableModal{
	@NonNull
	CompletableFuture<?> executeGuild(@NonNull ModalInteraction event, @NonNull Guild guild, @NonNull Member member) throws BotException;
}
