package fr.rakambda.rsndiscord.spring.interaction.button.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableButtonUser extends IExecutableButton{
	@NotNull
	CompletableFuture<?> executeUser(@NotNull ButtonInteraction event) throws BotException;
}
