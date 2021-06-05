package fr.raksrinana.rsndiscord;

import lombok.extern.log4j.Log4j2;

@Log4j2
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
			log.warn("Forcing shutdown");
			System.exit(0);
		}
		catch(InterruptedException e){
			log.warn("Failed to wait for forced shutdown", e);
		}
	}
}
