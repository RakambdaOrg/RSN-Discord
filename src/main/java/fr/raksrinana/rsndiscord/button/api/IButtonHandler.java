package fr.raksrinana.rsndiscord.button.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.impl.AniListMediaCompletedButtonHandler;
import fr.raksrinana.rsndiscord.button.impl.AniListMediaDiscardedButtonHandler;
import fr.raksrinana.rsndiscord.button.impl.ArchiveMediaReactionButtonHandler;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ArchiveMediaReactionButtonHandler.class, name = "ArchiveMediaReaction"),
		@JsonSubTypes.Type(value = AniListMediaCompletedButtonHandler.class, name = "AniListMediaCompleted"),
		@JsonSubTypes.Type(value = AniListMediaDiscardedButtonHandler.class, name = "AniListMediaDiscarded"),
})
public interface IButtonHandler{
	@NotNull
	String getButtonId();
	
	@NotNull
	Button asButton();
	
	@NotNull
	CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event);
}
