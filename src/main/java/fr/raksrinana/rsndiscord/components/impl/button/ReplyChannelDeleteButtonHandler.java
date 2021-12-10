package fr.raksrinana.rsndiscord.components.impl.button;

import fr.raksrinana.rsndiscord.components.ButtonHandler;
import fr.raksrinana.rsndiscord.components.ComponentResult;
import fr.raksrinana.rsndiscord.components.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.schedule.impl.DeleteChannelScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.components.ComponentResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
@ButtonHandler
@Deprecated
public class ReplyChannelDeleteButtonHandler extends SimpleButtonHandler{
	public ReplyChannelDeleteButtonHandler(){
		super("reply-channel-delete");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonClickEvent event, @NotNull Guild guild, @NotNull Member member){
		var channel = event.getTextChannel();
		
		var guildConfiguration = Settings.get(guild);
		
		return guildConfiguration.getArchiveCategory()
				.flatMap(CategoryConfiguration::getCategory)
				.map(archiveCategory -> JDAWrappers.edit(channel)
						.setParent(archiveCategory)
						.sync(archiveCategory)
						.submit()
						.thenCompose(future -> JDAWrappers.removeComponents(event).submit())
						.thenCompose(future -> JDAWrappers.reply(event, translate(guild, "reaction.archived", event.getMember().getAsMention())).submit())
						.thenAccept(m -> guildConfiguration.add(new DeleteChannelScheduleHandler(channel.getIdLong(), ZonedDateTime.now().plusDays(4))))
						.thenApply(e -> HANDLED))
				.orElseGet(() -> JDAWrappers.delete(channel).submit().thenApply(e -> HANDLED));
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.danger(getComponentId(), "Delete channel").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
