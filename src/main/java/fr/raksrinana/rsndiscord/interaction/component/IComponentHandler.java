package fr.raksrinana.rsndiscord.interaction.component;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public interface IComponentHandler<T extends Component, E extends ComponentInteraction>{
	@NotNull
	String getComponentId();
	
	@NotNull
	T asComponent();
	
	@NotNull
	default CompletableFuture<ComponentResult> handleGuild(@NotNull E event, @NotNull Guild guild, @NotNull Member member){
		return CompletableFuture.completedFuture(ComponentResult.NOT_IMPLEMENTED);
	}
	
	@NotNull
	default CompletableFuture<ComponentResult> handleUser(@NotNull E event){
		return CompletableFuture.completedFuture(ComponentResult.NOT_IMPLEMENTED);
	}
}
