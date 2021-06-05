package fr.raksrinana.rsndiscord.button.impl;

import fr.raksrinana.rsndiscord.button.ButtonHandler;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.button.ButtonResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
@ButtonHandler
public class TodoMessageKeepButtonHandler extends SimpleButtonHandler{
	public TodoMessageKeepButtonHandler(){
		super("todo-message-keep");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event){
		var guild = event.getGuild();
		var message = event.getMessage();
		var channel = event.getTextChannel();
		
		var forwardChannel = Settings.get(guild).getReactionsConfiguration().getSavedForwarding().get(new ChannelConfiguration(channel));
		return Optional.ofNullable(forwardChannel)
				.flatMap(ChannelConfiguration::getChannel)
				.map(c -> JDAWrappers.message(c, message)
						.clearActionRows()
						.submit()
						.thenApply(forwardedMessage -> true)
						.exceptionally(e -> false))
				.orElse(CompletableFuture.completedFuture(false))
				.thenAccept(forwarded -> {
					if(forwarded){
						JDAWrappers.delete(message).submit();
					}
					else{
						JDAWrappers.reply(event, translate(guild, "reaction.not-configured")).submit();
					}
				})
				.thenApply(empty -> HANDLED);
	}
	
	@Override
	@NotNull
	public Button asButton(){
		return Button.success(getButtonId(), "Archive").withEmoji(Emoji.fromUnicode("U+1F4E6"));
	}
}
