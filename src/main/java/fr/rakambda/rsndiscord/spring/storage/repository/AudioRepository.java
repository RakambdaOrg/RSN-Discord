package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.AudioEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AudioRepository extends JpaRepository<AudioEntity, Integer>{
	@NotNull
	Optional<AudioEntity> findByGuildId(long guildId);
	
	boolean existsByGuildId(long guildId);
}
