package fr.raksrinana.rsndiscord.button;

import fr.raksrinana.rsndiscord.button.api.IButtonHandler;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@EventListener
public class ButtonListener extends ListenerAdapter{
	@Override
	public void onButtonClick(@NotNull ButtonClickEvent event){
		super.onButtonClick(event);
		
		if(event.isFromGuild()){
			var componentId = event.getComponentId();
			Log.getLogger(event.getGuild()).info("Received button interaction {} from {}", componentId, event.getUser());
			
			Settings.get(event.getGuild()).getButtonHandler(componentId).ifPresentOrElse(
					handler -> event.deferReply().submit().thenAccept(empty -> handleClick(event, handler)),
					() -> JDAWrappers.reply(event, "Didn't find the interaction id " + componentId));
		}
	}
	
	private void handleClick(@NotNull ButtonClickEvent event, @NotNull IButtonHandler handler){
		try{
			var result = handler.handle(event);
			switch(result){
				case HANDLED -> {
					JDAWrappers.edit(event).submit();
					Settings.get(event.getGuild()).removeButtonHandler(handler.getButtonId());
				}
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to execute button {}", handler, e);
			JDAWrappers.edit(event, "Error executing button (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
		}
	}
}
