package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.StorageConfiguration;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import org.jetbrains.annotations.NotNull;
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
	@NotNull
	@Cacheable
	List<ChannelEntity> findAllByType(@NotNull ChannelType type);
	
	@NotNull
	@Cacheable
	List<ChannelEntity> findAllByGuild_IdAndType(long guildId, @NotNull ChannelType type);
	
	@CacheEvict
	boolean deleteAllByGuild_IdAndTypeAndChannelId(long guildId, @NotNull ChannelType type, long channelId);
	
	@CacheEvict
	boolean deleteAllByGuild_IdAndType(long guildId, @NotNull ChannelType type);
	
	@Override
	@CachePut
	@NotNull
	<S extends ChannelEntity> S save(@NotNull S entity);
}
