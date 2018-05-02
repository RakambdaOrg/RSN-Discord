package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import java.io.InvalidClassException;
import java.time.OffsetDateTime;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ReadyListener implements EventListener
{
	private int WAIT_TIME = 3600;
	
	@Override
	public void onEvent(Event event)
	{
		if(event instanceof ReadyEvent)
		{
			Thread t = new Thread(() -> {
				try
				{
					TextChannel trombiChannel = event.getJDA().getTextChannelById(new PhotoChannelConfig().getLong());
					//noinspection InfiniteLoopStatement
					while(true)
					{
						for(Message message : trombiChannel.getIterableHistory().cache(false))
							if(!message.isPinned())
								if(message.getCreationTime().isBefore(OffsetDateTime.now().minusDays(1)))
									message.delete().queue();
						
						Thread.sleep(WAIT_TIME * 1000);
					}
				}
				catch(InvalidClassException | NoValueDefinedException | InterruptedException e)
				{
					Log.error("Error getting photo channel", e);
				}
			});
			t.setDaemon(true);
			t.start();
		}
	}
}
