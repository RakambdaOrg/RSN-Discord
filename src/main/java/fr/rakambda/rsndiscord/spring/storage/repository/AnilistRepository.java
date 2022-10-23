package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.AnilistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnilistRepository extends JpaRepository<AnilistEntity, Long>{
}
