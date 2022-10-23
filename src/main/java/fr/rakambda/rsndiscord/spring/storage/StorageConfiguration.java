package fr.rakambda.rsndiscord.spring.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;

@Configuration
@EnableCaching
@Slf4j
public class StorageConfiguration{
	public static final String CHANNEL = "channel";
	
	@Bean
	public CacheManager cacheManager(){
		var cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(List.of(
				new ConcurrentMapCache(CHANNEL)
		));
		return cacheManager;
	}
	
	@CacheEvict(allEntries = true, value = {CHANNEL})
	@Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 500)
	public void reportCacheEvict(){
		log.info("Flushing JPA cache");
	}
}
