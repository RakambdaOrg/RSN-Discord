package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.ForceShutdownThread;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ShutdownListener extends ListenerAdapter{
	@Override
	public void onShutdown(@NotNull final ShutdownEvent event){
		super.onShutdown(event);
		getLogger(null).info("BOT STOPPED");
		new ForceShutdownThread().start();
	}
}
