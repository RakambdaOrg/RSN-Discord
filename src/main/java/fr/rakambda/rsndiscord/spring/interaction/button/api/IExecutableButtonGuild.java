package fr.rakambda.rsndiscord.spring.interaction.button.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableButtonGuild extends IExecutableButton{
	@NonNull
	CompletableFuture<?> executeGuild(@NonNull ButtonInteraction event, @NonNull Guild guild, @NonNull Member member) throws BotException;
}
