package fr.rakambda.rsndiscord.spring.interaction.modal.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableModalUser extends IExecutableModal{
	@NotNull
	CompletableFuture<?> executeUser(@NotNull ModalInteraction event) throws BotException;
}
