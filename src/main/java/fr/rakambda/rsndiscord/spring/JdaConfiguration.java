package fr.rakambda.rsndiscord.spring;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.audio.dave.DaveSessionFactory;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Slf4j
@Configuration
public class JdaConfiguration{
	@Bean
	public JDA getJda(List<ListenerAdapter> listenerAdapters, ApplicationSettings applicationSettings, DaveSessionFactory daveSessionFactory){
		log.info("Building JDA");
		var jda = JDABuilder.createDefault(applicationSettings.getBotToken())
				.enableCache(CacheFlag.EMOJI)
				.setAutoReconnect(true)
				.addEventListeners(listenerAdapters.toArray(Object[]::new))
				.setAudioModuleConfig(new AudioModuleConfig().withDaveSessionFactory(daveSessionFactory))
				.build();
		
		log.info("JDA built");
		return jda;
	}
	
	@Bean
	public DaveSessionFactory daveSessionFactory(){
		return new JDaveSessionFactory();
	}
}
