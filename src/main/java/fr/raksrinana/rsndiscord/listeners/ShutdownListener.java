package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.ForceShutdownThread;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ShutdownListener extends ListenerAdapter{
	@Override
	public void onShutdown(@Nonnull final ShutdownEvent event){
		super.onShutdown(event);
		Log.getLogger(null).info("BOT STOPPED");
		new ForceShutdownThread().start();
	}
}
