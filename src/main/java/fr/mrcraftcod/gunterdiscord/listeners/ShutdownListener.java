package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.IOException;
import java.util.logging.Handler;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ShutdownListener extends ListenerAdapter
{
	@Override
	public void onShutdown(ShutdownEvent event)
	{
		super.onShutdown(event);
		try
		{
			QuizMessageListener.setBack();
			Settings.save();
			for(Handler h : Log.getLogger().getHandlers())
				h.close();
			Log.info("BOT STOPPED");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			Settings.close();
		}
	}
}
