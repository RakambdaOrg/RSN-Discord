package fr.raksrinana.rsndiscord.button.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.command.impl.schedule.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import static fr.raksrinana.rsndiscord.button.ButtonResult.HANDLED;
import static fr.raksrinana.rsndiscord.button.ButtonResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.MINUTES;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ArchiveMediaReaction")
public class ArchiveMediaReactionButtonHandler extends SimpleButtonHandler{
	@NotNull
	@Override
	public ButtonResult handle(@NotNull ButtonClickEvent event){
		var guild = event.getGuild();
		var channel = event.getTextChannel();
		var user = event.getUser();
		
		return Settings.get(guild).getArchiveCategory()
				.flatMap(CategoryConfiguration::getCategory)
				.map(archiveCategory -> JDAWrappers.edit(channel)
						.setParent(archiveCategory)
						.sync(archiveCategory)
						.submit()
						.thenCompose(future -> JDAWrappers.reply(event, translate(guild, "reaction.archived", user.getAsMention())).submit())
						.thenAccept(m -> ChannelCommand.scheduleDeletion(ZonedDateTime.now().plusWeeks(2), channel, user))
						.thenApply(empty -> HANDLED)
						.completeOnTimeout(SUCCESS, 1, MINUTES)
						.join())
				.orElseGet(() -> {
					JDAWrappers.message(channel, translate(guild, "reaction.no-archive")).submit()
							.thenAccept(ScheduleUtils.deleteMessage(date -> date.plusMinutes(5)));
					return SUCCESS;
				});
	}
	
	@Override
	@NotNull
	public Button asButton(){
		return Button.danger(getButtonId(), "Archive");
	}
}
