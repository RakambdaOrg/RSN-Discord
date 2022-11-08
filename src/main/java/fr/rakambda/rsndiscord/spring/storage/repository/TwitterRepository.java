package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.TwitterEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.TwitterType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TwitterRepository extends JpaRepository<TwitterEntity, Integer>{
	@NotNull
	List<TwitterEntity> findAllByType(@NotNull TwitterType type);
	
	@NotNull
	List<TwitterEntity> findAllByGuildIdAndType(long guildId, @NotNull TwitterType type);
	
	int deleteAllByGuildIdAndTypeAndSearch(long guildId, @NotNull TwitterType type, String search);
	
	int deleteAllByGuildIdAndType(long guildId, @NotNull TwitterType type);
}
