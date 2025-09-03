package fr.rakambda.rsndiscord.spring.interaction.modal.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableModalUser extends IExecutableModal{
	@NonNull
	CompletableFuture<?> executeUser(@NonNull ModalInteraction event) throws BotException;
}
