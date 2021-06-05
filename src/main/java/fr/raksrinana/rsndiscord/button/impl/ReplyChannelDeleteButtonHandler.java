package fr.raksrinana.rsndiscord.button.impl;

import fr.raksrinana.rsndiscord.button.ButtonHandler;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.scheduleaction.impl.DeleteChannelScheduleActionHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.button.ButtonResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
@ButtonHandler
public class ReplyChannelDeleteButtonHandler extends SimpleButtonHandler{
	public ReplyChannelDeleteButtonHandler(){
		super("reply-channel-delete");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event){
		var guild = event.getGuild();
		var user = event.getUser();
		var message = event.getMessage();
		var channel = event.getTextChannel();
		
		var guildConfiguration = Settings.get(guild);
		
		return guildConfiguration.getArchiveCategory()
				.flatMap(CategoryConfiguration::getCategory)
				.map(archiveCategory -> JDAWrappers.edit(channel)
						.setParent(archiveCategory)
						.sync(archiveCategory)
						.submit()
						.thenCompose(future -> JDAWrappers.message(channel, translate(guild, "reaction.archived", event.getMember().getAsMention())).submit())
						.thenAccept(m -> guildConfiguration.add(new DeleteChannelScheduleActionHandler(channel.getIdLong(), ZonedDateTime.now().plusDays(4))))
						.thenApply(e -> HANDLED))
				.orElseGet(() -> JDAWrappers.delete(channel).submit().thenApply(e -> HANDLED));
	}
	
	@Override
	@NotNull
	public Button asButton(){
		return Button.danger(getButtonId(), "Discard").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
