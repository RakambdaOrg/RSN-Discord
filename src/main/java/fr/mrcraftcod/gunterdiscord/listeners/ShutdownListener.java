package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.listeners.musicparty.MusicPartyListener;
import fr.mrcraftcod.gunterdiscord.listeners.quiz.QuizListener;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class ShutdownListener extends ListenerAdapter{
	@Override
	public void onShutdown(final ShutdownEvent event){
		super.onShutdown(event);
		try{
			Main.close();
			QuizListener.stopAll();
			HangmanListener.stopAll();
			MusicPartyListener.stopAll();
			event.getJDA().getGuilds().forEach(GunterAudioManager::leave);
			getLogger(null).info("BOT STOPPED");
		}
		catch(final Exception e){
			getLogger(null).error("", e);
		}
	}
}
