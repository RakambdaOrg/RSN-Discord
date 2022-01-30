package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.schedule.impl.DeleteChannelScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.ZonedDateTime.now;

@Log4j2
@ButtonHandler
public class ArchiveMediaReactionButtonHandler extends SimpleButtonHandler{
	public ArchiveMediaReactionButtonHandler(){
		super("archive-media-reaction");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var channel = event.getTextChannel();
		var user = event.getUser();
		
		var config = Settings.get(guild);
		
		log.info("Archiving channel {}", channel);
		
		return config.getArchiveCategory()
				.flatMap(CategoryConfiguration::getCategory)
				.map(archiveCategory -> JDAWrappers.edit(channel)
						.setParent(archiveCategory)
						.sync(archiveCategory)
						.submit()
						.thenCompose(future -> JDAWrappers.removeComponents(event).submit())
						.thenCompose(future -> JDAWrappers.reply(event, translate(guild, "reaction.archived", user.getAsMention())).submit())
						.thenAccept(m -> config.add(new DeleteChannelScheduleHandler(channel.getIdLong(), now().plusWeeks(2))))
						.thenApply(empty -> ComponentResult.HANDLED))
				.orElseGet(() -> {
					JDAWrappers.edit(event, translate(guild, "reaction.no-archive")).submitAndDelete(5);
					return CompletableFuture.completedFuture(ComponentResult.HANDLED);
				});
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.danger(getComponentId(), "Archive").withEmoji(Emoji.fromUnicode("U+1F4E6"));
	}
}
