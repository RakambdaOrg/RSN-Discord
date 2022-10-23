package fr.rakambda.rsndiscord.spring;

import fr.rakambda.rsndiscord.spring.audio.AudioService;
import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.interaction.slash.SlashCommandService;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class StopService{
	private final ApplicationSettings applicationSettings;
	private final SlashCommandService slashCommandService;
	private final AudioServiceFactory audioServiceFactory;
	private final ApplicationContext applicationContext;
	
	@Autowired
	public StopService(ApplicationSettings applicationSettings, @Lazy SlashCommandService slashCommandService, AudioServiceFactory audioServiceFactory, ApplicationContext applicationContext){
		this.applicationSettings = applicationSettings;
		this.slashCommandService = slashCommandService;
		this.audioServiceFactory = audioServiceFactory;
		this.applicationContext = applicationContext;
	}
	
	public void shutdown(@NotNull JDA jda){
		log.info("Stopping bot");
		audioServiceFactory.getAll().forEach(AudioService::leave);
		resetCommands(jda).thenAccept(empty -> jda.shutdown());
		
		var client = jda.getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
		
		SpringApplication.exit(applicationContext, () -> 0);
	}
	
	public void startShutdownTimeout(){
		new ForceShutdownThread().start();
	}
	
	@NotNull
	private CompletableFuture<Void> resetCommands(@NotNull JDA jda){
		if(!applicationSettings.isDevelopment()){
			return CompletableFuture.completedFuture(null);
		}
		
		return slashCommandService.removeAllCommands(jda);
	}
	
	@Slf4j
	private static class ForceShutdownThread extends Thread{
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
}
