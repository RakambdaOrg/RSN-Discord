package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.StopService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShutdownEventListener extends ListenerAdapter{
	private final StopService stopService;
	
	public ShutdownEventListener(StopService stopService){
		this.stopService = stopService;
	}
	
	@Override
	public void onShutdown(@NotNull ShutdownEvent event){
		log.info("JDA shutting down");
		stopService.startShutdownTimeout();
	}
}
