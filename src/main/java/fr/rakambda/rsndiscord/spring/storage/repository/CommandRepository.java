package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.CommandEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<CommandEntity, Long>{
	@NotNull
	List<CommandEntity> findByGuildId(long guildId);
	
	@NotNull
	Optional<CommandEntity> findByGuildIdAndName(long guildId, @NotNull String name);
	
	int deleteAllByGuildIdAndName(long guildId, @NotNull String name);
	
	void deleteAllByGuildId(long guildId);
}
