package fr.rakambda.rsndiscord.spring.interaction.modal.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableModalGuild extends IExecutableModal{
	@NotNull
	CompletableFuture<?> executeGuild(@NotNull ModalInteraction event, @NotNull Guild guild, @NotNull Member member) throws BotException;
}
