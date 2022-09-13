package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.schedule.impl.DeleteThreadScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Log4j2
@ButtonHandler
@Deprecated
public class ClearDeleteThreadCancelButtonHandler extends SimpleButtonHandler{
	public ClearDeleteThreadCancelButtonHandler(){
		super("clear-delete-thread-cancel");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		if(event.getChannelType() == ChannelType.GUILD_PUBLIC_THREAD){
			var threadId = event.getChannel().getIdLong();
			
			var iterator = Settings.get(guild).getScheduleHandlers().elements().asIterator();
			while(iterator.hasNext()){
				var handler = iterator.next();
				if(handler instanceof DeleteThreadScheduleHandler deleteThreadScheduleHandler){
					if(Objects.equals(deleteThreadScheduleHandler.getThreadId(), threadId)){
						iterator.remove();
					}
				}
			}
		}
		return CompletableFuture.completedFuture(ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.danger(getComponentId(), "Cancel").withEmoji(Emoji.fromUnicode("U+274C"));
	}
}
