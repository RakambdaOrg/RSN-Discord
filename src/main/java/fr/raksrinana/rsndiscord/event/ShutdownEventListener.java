package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.ForceShutdownThread;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@EventListener
@Log4j2
public class ShutdownEventListener extends ListenerAdapter{
	@Override
	public void onShutdown(@NotNull ShutdownEvent event){
		super.onShutdown(event);
		log.info("JDA disconnected");
		new ForceShutdownThread().start();
	}
}
