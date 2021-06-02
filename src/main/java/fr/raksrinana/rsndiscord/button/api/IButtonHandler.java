package fr.raksrinana.rsndiscord.button.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.impl.ArchiveMediaReactionButtonHandler;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ArchiveMediaReactionButtonHandler.class, name = "ArchiveMediaReaction"),
})
public interface IButtonHandler{
	@NotNull
	String getButtonId();
	
	@NotNull
	Button asButton();
	
	@NotNull
	ButtonResult handle(@NotNull ButtonClickEvent event);
}
