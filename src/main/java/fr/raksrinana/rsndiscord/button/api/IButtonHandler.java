package fr.raksrinana.rsndiscord.button.api;

import fr.raksrinana.rsndiscord.button.ButtonResult;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IButtonHandler{
	@NotNull
	String getButtonId();
	
	@NotNull
	Button asButton();
	
	@NotNull
	CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event);
}
