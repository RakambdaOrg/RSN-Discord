package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.JDA;
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
	// private final ScheduledExecutorService service;
	
	public ShutdownListener(JDA jda)
	{
		// final List<String> messages = List.of("Hey %s, chat va bien ?", "Oh tu sais quoi %s ? Hier soir j'ai mangé des boobs à la framboise !", "La vache %s, je sais pas si je vais réussir à dormir ce soir, tu me tiens companie ?", "Bonsoir %s", "Bonjour %s", "Tu parles trop %s", "Surout n'oublie pas, sors protégé %s", "Hey %s tu peux me raconter une histoire ?", "Miaou %s", "Oh mince %s, je crois je t'ai pin, mince alors", "%s ? %s ? C'était pour savoir si tu dormais", "%s? Tu as mis où les somnifaires ?", "%s Réveille toi! Tu ronfles et c'est insuportable !", "%s, tu m'en veux de te pin ?", "%s, avoue tu regrettes tes everyone !", "%s tu sais, à la fin de cette punition tu vas me manquer :frowning2:", "%s, tu connais l'histoire de toto aux toilettes ?", "%s, j'ai retrouvé ta sextape, j'en fais quoi, je la publie ?", "%s mange 5 fruits et légumes par jour! (www.mangerbouger.com)", "%s ne prends pas ce somnifère avant d'avoir demandé conseil à ton spécialiste et avoir lu la notice d'embalage", "%s t'es un voilà !");
		// service = Executors.newSingleThreadScheduledExecutor();
		// service.scheduleAtFixedRate(() -> {
		// 	try
		// 	{
		// 		User user = jda.getUserById(288737124444798976L);
		// 		jda.getGuildById(448124547322085397L).getTextChannelById(448130342512230417L).sendMessageFormat(messages.get(ThreadLocalRandom.current().nextInt(messages.size())), user.getAsMention()).complete();
		// 	}
		// 	catch(Exception e)
		// 	{
		// 		Log.error("BRIX", e);
		// 	}
		// }, 1, 15, TimeUnit.MINUTES);
	}
	
	@Override
	public void onShutdown(ShutdownEvent event)
	{
		super.onShutdown(event);
		try
		{
			// service.shutdownNow();
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
