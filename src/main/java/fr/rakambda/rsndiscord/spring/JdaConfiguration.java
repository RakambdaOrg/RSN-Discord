package fr.rakambda.rsndiscord.spring;

import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Slf4j
@Configuration
public class JdaConfiguration{
	@Bean
	public JDA getJda(List<ListenerAdapter> listenerAdapters, ApplicationSettings applicationSettings) throws InterruptedException{
		log.info("Building JDA");
		var jda = JDABuilder.createDefault(applicationSettings.getBotToken())
				.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.enableCache(CacheFlag.EMOJI)
				.setAutoReconnect(true)
				.addEventListeners(listenerAdapters.toArray(Object[]::new))
				.build();
		
		log.info("JDA built");
		return jda;
	}
}
