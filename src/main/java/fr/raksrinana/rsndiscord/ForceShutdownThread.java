package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.utils.log.Log;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-25.
 *
 * @author Thomas Couchoud
 * @since 2018-10-25
 */
public class ForceShutdownThread extends Thread{
	private static final int TIMEOUT_SHUTDOWN = 30000;
	
	public ForceShutdownThread(){
		this.setDaemon(true);
	}
	
	@Override
	public void run(){
		try{
			Thread.sleep(TIMEOUT_SHUTDOWN);
			Log.getLogger(null).warn("Forcing shutdown");
			System.exit(0);
		}
		catch(final InterruptedException e){
			Log.getLogger(null).warn("", e);
		}
	}
}
