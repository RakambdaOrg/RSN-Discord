package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class SaveConfigScheduledRunner implements ScheduledRunner{
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveConfigScheduledRunner.class);
	
	/**
	 * Constructor.
	 */
	public SaveConfigScheduledRunner(){
		getLogger(null).info("Creating saver runner");
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
	
	@Override
	public long getPeriod(){
		return 5;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
}
