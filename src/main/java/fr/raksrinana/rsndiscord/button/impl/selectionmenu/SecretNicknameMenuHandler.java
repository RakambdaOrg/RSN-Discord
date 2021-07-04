package fr.raksrinana.rsndiscord.button.impl.selectionmenu;

import fr.raksrinana.rsndiscord.button.ComponentResult;
import fr.raksrinana.rsndiscord.button.SelectionMenuHandler;
import fr.raksrinana.rsndiscord.button.base.SimpleSelectionMenuHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.button.ComponentResult.HANDLED;

@Log4j2
@SelectionMenuHandler
public class SecretNicknameMenuHandler extends SimpleSelectionMenuHandler{
	public SecretNicknameMenuHandler(){
		super("secretnick");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handle(@NotNull SelectionMenuEvent event){
		return JDAWrappers.edit(event, "Nickname chosen").clearActionRows().submit()
				.thenCompose(message -> switch(event.getValues().get(0)){
					case "1" -> JDAWrappers.modifyNickname(event.getMember(), "I got fucked").submit();
					case "2" -> JDAWrappers.modifyNickname(event.getMember(), "I got fucked really hard").submit();
					case "3" -> JDAWrappers.modifyNickname(event.getMember(), "Rak isn't cool").submit();
					case "4" -> JDAWrappers.modifyNickname(event.getMember(), "Rak the best").submit();
					default -> JDAWrappers.modifyNickname(event.getMember(), "WTF is that").submit();
				})
				.thenApply(message -> HANDLED);
	}
	
	@Override
	@NotNull
	public SelectionMenu asComponent(){
		return SelectionMenu.create("secretnick")
				.setPlaceholder("Select your new nickname")
				.setRequiredRange(1, 1)
				.addOptions(SelectOption.of("I got fucked", "1"))
				.addOption("I got fucked really hard", "2")
				.addOption("Rak isn't cool", "3")
				.addOptions(SelectOption.of("Rak the best", "4").withEmoji(Emoji.fromUnicode("\uD83D\uDC96")))
				.build();
	}
}
