package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Settings;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.IOException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ShutdownListener extends ListenerAdapter
{
	private final Settings settings;
	
	public ShutdownListener(Settings settings)
	{
		this.settings = settings;
	}
	
	@Override
	public void onShutdown(ShutdownEvent event)
	{
		super.onShutdown(event);
		try
		{
			settings.save();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			settings.close();
		}
	}
}
