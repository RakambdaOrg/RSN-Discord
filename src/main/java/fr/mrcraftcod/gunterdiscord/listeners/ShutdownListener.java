package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ShutdownListener extends ListenerAdapter
{
	private final ScheduledExecutorService service;
	
	public ShutdownListener(JDA jda)
	{
		service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			try
			{
				List<String> messages = List.of("Hey %s, chat va bien?", "Oh tu sais quoi %s? Hier soir j'ai mangé des boobs à la framboise!", "La vache %s, je sais pas si je vais réussir à dormir ce soir, tu me tiens companie?", "Bonsoir %s", "Bonjour %s", "Tu parles trop %s", "Surout n'oublie pas, sors protégé %s", "Hey %s tu peux me raconter une histoire?", "Miaou %s", "Oh mince %s, je crois je t'ai pin, mince alors");
				User user = jda.getUserById(288737124444798976L);
				jda.getGuildById(448124547322085397L).getTextChannelById(448130342512230417L).sendMessageFormat(messages.get(ThreadLocalRandom.current().nextInt(messages.size())), user.getAsMention()).complete();
			}
			catch(Exception e)
			{
				Log.error("BRIX", e);
			}
		}, 1, 15, TimeUnit.MINUTES);
	}
	
	@Override
	public void onShutdown(ShutdownEvent event)
	{
		super.onShutdown(event);
		try
		{
			service.shutdownNow();
			QuizListener.stopAll();
			HangmanListener.stopAll();
			Settings.save();
			for(Handler h : Log.getLogger().getHandlers())
				h.close();
			Log.info("BOT STOPPED");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
		finally
		{
			Settings.close();
		}
	}
}
