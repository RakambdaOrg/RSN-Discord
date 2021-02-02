package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.log.Log;

/**
 * Thread used to force the shutdown of the application after a delay.
 */
public class ForceShutdownThread extends Thread{
	private static final int TIMEOUT_SHUTDOWN = 30000;
	
	public ForceShutdownThread(){
		setDaemon(true);
		setName("Force shutdown");
	}
	
	@Override
	public void run(){
		try{
			Thread.sleep(TIMEOUT_SHUTDOWN);
			Log.getLogger(null).warn("Forcing shutdown");
			System.exit(0);
		}
		catch(InterruptedException e){
			Log.getLogger(null).warn("Failed to wait for forced shutdown", e);
		}
	}
}
