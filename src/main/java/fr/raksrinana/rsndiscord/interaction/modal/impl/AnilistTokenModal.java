package fr.raksrinana.rsndiscord.interaction.modal.impl;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.interaction.modal.ModalResult;
import fr.raksrinana.rsndiscord.interaction.modal.api.ModalHandler;
import fr.raksrinana.rsndiscord.interaction.modal.base.SimpleModalHandler;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.interaction.modal.ModalResult.FAILED;
import static fr.raksrinana.rsndiscord.interaction.modal.ModalResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
@ModalHandler
public class AnilistTokenModal extends SimpleModalHandler{
	private static final String CODE_VALUE_ID = "code";
	
	public AnilistTokenModal(){
		super("anilist-register-token");
	}
	
	@Override
	@NotNull
	public CompletableFuture<ModalResult> handleGuild(@NotNull ModalInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var code = event.getValue(CODE_VALUE_ID).getAsString();
		
		try{
			AniListApi.requestToken(member, code);
			return JDAWrappers.reply(event, translate(guild, "anilist.api-code.saved"))
					.ephemeral(true)
					.submit()
					.thenApply(empty -> HANDLED);
		}
		catch(IllegalArgumentException e){
			return JDAWrappers.reply(event, translate(guild, "anilist.api-code.invalid"))
					.ephemeral(true)
					.submit()
					.thenApply(empty -> HANDLED);
		}
		catch(InvalidResponseException e){
			log.error("Error getting AniList access token", e);
			Utilities.reportException("Error getting AniList Token", e);
			
			return JDAWrappers.reply(event, translate(guild, "anilist.api-code.save-error"))
					.ephemeral(true)
					.submit()
					.thenApply(empty -> FAILED);
		}
	}
	
	@Override
	@NotNull
	public Modal asComponent(){
		var code = TextInput.create(CODE_VALUE_ID, "Auth code", TextInputStyle.SHORT)
				.setValue("Get your code at : " + AniListApi.getCODE_LINK())
				.setRequired(true)
				.build();
		
		return Modal.create(getComponentId(), "Anilist register")
				.addActionRows(ActionRow.of(code))
				.build();
	}
}
