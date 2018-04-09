package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ReadyListener implements EventListener
{
	@Override
	public void onEvent(Event event)
	{
		if (event instanceof ReadyEvent)
			System.out.println("API is ready!");
	}
}
