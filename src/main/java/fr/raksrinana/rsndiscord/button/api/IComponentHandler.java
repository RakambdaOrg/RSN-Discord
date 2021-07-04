package fr.raksrinana.rsndiscord.button.api;

import fr.raksrinana.rsndiscord.button.ComponentResult;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.Component;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IComponentHandler<T extends Component, E extends GenericComponentInteractionCreateEvent>{
	@NotNull
	String getComponentId();
	
	@NotNull
	T asComponent();
	
	@NotNull
	CompletableFuture<ComponentResult> handle(@NotNull E event);
}
