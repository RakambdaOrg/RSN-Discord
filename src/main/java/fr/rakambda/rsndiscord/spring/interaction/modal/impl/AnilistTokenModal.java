package fr.rakambda.rsndiscord.spring.interaction.modal.impl;

import fr.rakambda.rsndiscord.spring.api.anilist.AnilistService;
import fr.rakambda.rsndiscord.spring.api.exceptions.AuthFailureException;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.interaction.modal.api.IExecutableModalGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class AnilistTokenModal implements IExecutableModalGuild{
	private static final String COMPONENT_ID = "anilist-register-token";
	private static final String CODE_OPTION = "code";
	
	private final LocalizationService localizationService;
	private final AnilistService aniListService;
	
	@Autowired
	public AnilistTokenModal(LocalizationService localizationService, AnilistService aniListService){
		this.localizationService = localizationService;
		this.aniListService = aniListService;
	}
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull ModalInteraction event, @NotNull Guild guild, @NotNull Member member) throws RequestFailedException, AuthFailureException{
		var deferred = event.deferReply(true).submit();
		
		var code = event.getValue(CODE_OPTION).getAsString();
		var locale = event.getUserLocale();
		
		aniListService.login(event.getUser().getIdLong(), code);
		return deferred.thenCompose(empty -> JDAWrappers
				.reply(event, localizationService.translate(locale, "anilist.api-code.saved"))
				.ephemeral(true)
				.submit());
	}
	
	@NotNull
	public static Supplier<Modal> builder(String codeLink){
		return () -> {
			var code = TextInput.create(CODE_OPTION, "Auth code", TextInputStyle.PARAGRAPH)
					.setValue("Get your code at : " + codeLink)
					.setRequired(true)
					.build();
			
			return Modal.create(COMPONENT_ID, "Anilist register")
					.addComponents(ActionRow.of(code))
					.build();
		};
	}
}
