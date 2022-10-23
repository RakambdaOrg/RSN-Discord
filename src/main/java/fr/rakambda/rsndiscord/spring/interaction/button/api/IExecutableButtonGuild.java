package fr.rakambda.rsndiscord.spring.interaction.button.api;

import fr.rakambda.rsndiscord.spring.BotException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IExecutableButtonGuild extends IExecutableButton{
	@NotNull
	CompletableFuture<?> executeGuild(@NotNull ButtonInteraction event, @NotNull Guild guild, @NotNull Member member) throws BotException;
}
