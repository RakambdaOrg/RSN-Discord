package fr.rakambda.rsndiscord.spring.interaction.button.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jspecify.annotations.NonNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableButtonUser extends IExecutableButton{
	@NonNull
	CompletableFuture<?> executeUser(@NonNull ButtonInteraction event) throws BotException;
}
