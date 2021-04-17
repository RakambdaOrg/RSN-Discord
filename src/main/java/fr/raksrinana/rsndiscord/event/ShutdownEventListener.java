package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.ForceShutdownThread;
import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@EventListener
public class ShutdownEventListener extends ListenerAdapter{
	@Override
	public void onShutdown(@NotNull ShutdownEvent event){
		super.onShutdown(event);
		Log.getLogger().info("BOT STOPPED");
		new ForceShutdownThread().start();
	}
}
