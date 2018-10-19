package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class SaveConfigScheduledRunner implements Runnable{
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveConfigScheduledRunner.class);
	private final JDA jda;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	public SaveConfigScheduledRunner(final JDA jda){
		getLogger(null).info("Creating roles runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		try{
			Settings.save();
		}
		catch(final IOException e){
			LOGGER.error("Failed to save settings", e);
		}
	}
}
