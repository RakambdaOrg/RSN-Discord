package fr.mrcraftcod.gunterdiscord;

import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-25.
 *
 * @author Thomas Couchoud
 * @since 2018-10-25
 */
public class ForceShutdownThread extends Thread{
	public ForceShutdownThread(){
		this.setDaemon(true);
	}
	
	@Override
	public void run(){
		try{
			Thread.sleep(30000);
			getLogger(null).warn("Forcing shutdown");
			System.exit(0);
		}
		catch(final InterruptedException e){
			getLogger(null).warn("", e);
		}
	}
}
