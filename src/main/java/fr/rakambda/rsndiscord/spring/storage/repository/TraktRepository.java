package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.TraktEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TraktRepository extends JpaRepository<TraktEntity, Long>{
	@NotNull
	List<TraktEntity> findAllByEnabledIsTrue();
}
