package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.StorageConfiguration;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@CacheConfig(cacheNames = StorageConfiguration.CHANNEL)
public interface ChannelRepository extends JpaRepository<ChannelEntity, Integer>{
	@NonNull
	@Cacheable
	List<ChannelEntity> findAllByType(@NonNull ChannelType type);
	
	@NonNull
	@Cacheable
	List<ChannelEntity> findAllByGuildIdAndType(long guildId, @NonNull ChannelType type);
	
	@CacheEvict
	int deleteAllByGuildIdAndTypeAndChannelId(long guildId, @NonNull ChannelType type, long channelId);
	
	@CacheEvict
	int deleteAllByGuildIdAndType(long guildId, @NonNull ChannelType type);
	
	@Override
	@CachePut
	@NonNull
	<S extends ChannelEntity> S save(@NonNull S entity);
}
