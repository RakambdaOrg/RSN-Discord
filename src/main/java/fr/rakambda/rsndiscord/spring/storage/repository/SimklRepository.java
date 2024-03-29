package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.SimklEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimklRepository extends JpaRepository<SimklEntity, Long>{
	@NotNull
	List<SimklEntity> findAllByEnabledIsTrue();
}
