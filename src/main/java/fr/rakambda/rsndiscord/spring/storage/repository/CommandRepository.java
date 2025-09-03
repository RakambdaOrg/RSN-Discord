package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.CommandEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<CommandEntity, Long>{
	@NonNull
	List<CommandEntity> findByGuildId(long guildId);
	
	@NonNull
	Optional<CommandEntity> findByGuildIdAndName(long guildId, @NonNull String name);
	
	int deleteAllByGuildIdAndName(long guildId, @NonNull String name);
	
	void deleteAllByGuildId(long guildId);
}
