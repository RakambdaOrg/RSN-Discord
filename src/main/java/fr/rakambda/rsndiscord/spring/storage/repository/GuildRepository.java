package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.GuildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends JpaRepository<GuildEntity, Long>{
}
