package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.RssEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RssRepository extends JpaRepository<RssEntity, Integer>{
	@NotNull
	List<RssEntity> findAllByGuild_Id(long guildId);
	
	int deleteAllByGuild_IdAndUrl(long guildId, String url);
	
	int deleteAllByGuild_Id(long guildId);
}
