package fr.raksrinana.rsndiscord.button;

import fr.raksrinana.rsndiscord.event.EventListener;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@EventListener
public class ButtonListener extends ListenerAdapter{
	@Override
	public void onButtonClick(@NotNull ButtonClickEvent event){
		super.onButtonClick(event);
		
	}
}
