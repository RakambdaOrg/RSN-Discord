package fr.raksrinana.rsndiscord.button.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.button.ButtonHandler;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.button.ButtonResult.HANDLED_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("AniListMediaDiscarded")
@Log4j2
@ButtonHandler
public class AniListMediaDiscardedButtonHandler extends SimpleButtonHandler{
	public AniListMediaDiscardedButtonHandler(){
		super("anilist-media-discarded");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event){
		var user = event.getUser();
		var message = event.getMessage();
		
		return Optional.ofNullable(event.getJDA().getUserById(MAIN_RAKSRINANA_ACCOUNT))
				.map(User::openPrivateChannel)
				.map(RestAction::submit)
				.map(future -> future.thenCompose(privateChannel -> JDAWrappers.message(privateChannel, user.getAsMention() + " discarded").submit()))
				.map(future -> future.thenCompose(m -> JDAWrappers.message(m.getPrivateChannel(), message).setActionRows().submit()))
				.map(future -> future.thenApply(m -> HANDLED_NO_MESSAGE))
				.orElseGet(() -> CompletableFuture.completedFuture(HANDLED_NO_MESSAGE));
	}
	
	@Override
	@NotNull
	public Button asButton(){
		return Button.danger(getButtonId(), "Discard").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
