package fr.rakambda.rsndiscord.spring.storage.repository;

import fr.rakambda.rsndiscord.spring.storage.entity.HermitcraftVideoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HermitcraftVideoRepository extends JpaRepository<HermitcraftVideoEntity, String>{
	@Override
	boolean existsById(@NotNull String s);
}
