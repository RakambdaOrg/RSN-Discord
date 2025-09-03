package fr.rakambda.rsndiscord.spring.audio;

import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import fr.rakambda.rsndiscord.spring.settings.MusicSettings;
import fr.rakambda.rsndiscord.spring.storage.repository.AudioRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AudioServiceFactory{
	private final AudioRepository audioRepository;
	private final MusicSettings musicSettings;
	private final Map<Long, AudioService> services;
	
	public AudioServiceFactory( AudioRepository audioRepository, ApplicationSettings applicationSettings){
		this.audioRepository = audioRepository;
		musicSettings = applicationSettings.getMusic();
		
		services = new ConcurrentHashMap<>();
	}
	
	@NonNull
	public AudioService get(@NonNull Guild guild){
		return services.computeIfAbsent(guild.getIdLong(), id -> buildService(guild));
	}
	
	@NonNull
	private AudioService buildService(@NonNull Guild guild){
		var guildEntity = audioRepository.findByGuildId(guild.getIdLong()).orElseThrow();
		var volume = Math.min(100, Math.max(0, guildEntity.getVolume()));
		
		return new AudioService(guild, volume, musicSettings);
	}
	
	@NonNull
	public Collection<AudioService> getAll(){
		return services.values();
	}
}
